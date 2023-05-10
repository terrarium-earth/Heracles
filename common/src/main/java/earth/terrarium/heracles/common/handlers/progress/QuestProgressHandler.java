package earth.terrarium.heracles.common.handlers.progress;

import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuestProgressHandler extends SavedData {

    private final Map<UUID, QuestsProgress> progress = new HashMap<>();

    public QuestProgressHandler() {
    }

    public QuestsProgress getProgress(UUID uuid) {
        return progress.computeIfAbsent(uuid, u -> new QuestsProgress(new HashMap<>()));
    }

    public static QuestsProgress getProgress(MinecraftServer server, UUID uuid) {
        QuestProgressHandler handler = read(server);
        return handler.getProgress(uuid);
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        // This is called whenever the world is saved.
        QuestHandler.save();
        for (var entry : progress.entrySet()) {
            CompoundTag progressTag = new CompoundTag();
            entry.getValue().progress().forEach((id, progress) -> {
                Quest quest = QuestHandler.get(id);
                if (quest == null) return;
                progressTag.put(id, QuestProgress.codec(quest).encodeStart(NbtOps.INSTANCE, progress)
                    .getOrThrow(false, System.err::println));
            });
            tag.put(entry.getKey().toString(), progressTag);
        }

        return tag;
    }

    public void load(CompoundTag tag) {
        for (var player : tag.getAllKeys()) {
            CompoundTag progress = tag.getCompound(player);
            Map<String, QuestProgress> questProgress = new HashMap<>();
            for (var quest : progress.getAllKeys()) {
                Quest questObj = QuestHandler.get(quest);
                if (questObj == null) {
                    System.err.println("Quest " + quest + " does not exist!");
                    continue;
                }
                questProgress.put(quest, QuestProgress.codec(questObj).parse(NbtOps.INSTANCE, progress.getCompound(quest))
                    .getOrThrow(false, System.err::println));
            }
            this.progress.put(UUID.fromString(player), new QuestsProgress(questProgress));
        }
    }

    public static QuestProgressHandler read(MinecraftServer server) {
        return server
            .overworld()
            .getDataStorage()
            .computeIfAbsent(tag -> {
                QuestProgressHandler handler = new QuestProgressHandler();
                handler.load(tag);
                return handler;
            }, QuestProgressHandler::new, "heracles_quest_progress");
    }
}
