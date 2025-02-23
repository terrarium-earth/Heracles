package earth.terrarium.heracles.common.handlers.pinned;

import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.pinned.SyncPinnedQuestsPacket;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PinnedQuestHandler extends SavedData {

    private final Map<UUID, Set<String>> pinned = new HashMap<>();

    public static Set<String> getPinned(ServerPlayer player) {
        return read(player.server).pinned.computeIfAbsent(player.getUUID(), u -> new LinkedHashSet<>());
    }

    public static void syncIfChanged(ServerPlayer player, Collection<String> pinned) {
        Set<String> pinnedSet = getPinned(player);
        for (String s : pinned) {
            if (!pinnedSet.contains(s)) {
                sync(player);
                return;
            }
        }
    }

    public static void sync(ServerPlayer player) {
        Map<String, QuestProgress> pinned = new LinkedHashMap<>();
        getPinned(player).forEach(id -> {
            Quest quest = QuestHandler.get(id);
            if (quest == null) return;
            pinned.put(id, QuestProgressHandler.getProgress(player.server, player.getUUID()).getProgress(id));
        });
        NetworkHandler.CHANNEL.sendToPlayer(new SyncPinnedQuestsPacket(pinned), player);
    }

    public PinnedQuestHandler() {}

    public PinnedQuestHandler(CompoundTag tag, HolderLookup.Provider registryProvider) {
        for (var player : tag.getAllKeys()) {
            Set<String> pinned = new LinkedHashSet<>();
            ListTag pinnedQuest = tag.getList(player, 8);
            for (int i = 0; i < pinnedQuest.size(); i++) {
                pinned.add(pinnedQuest.getString(i));
            }
            this.pinned.put(UUID.fromString(player), pinned);
        }
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        for (var entry : this.pinned.entrySet()) {
            ListTag pinnedQuest = new ListTag();
            for (String s : entry.getValue()) {
                pinnedQuest.add(StringTag.valueOf(s));
            }
            tag.put(entry.getKey().toString(), pinnedQuest);
        }

        return tag;
    }

    public static PinnedQuestHandler read(MinecraftServer server) {
        return server
            .overworld()
            .getDataStorage()
            .computeIfAbsent(new SavedData.Factory<>(
                PinnedQuestHandler::new,
                PinnedQuestHandler::new,
                null
            ), "heracles_pinned_quests");
    }
}
