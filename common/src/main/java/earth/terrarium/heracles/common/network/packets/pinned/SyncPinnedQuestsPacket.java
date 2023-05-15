package earth.terrarium.heracles.common.network.packets.pinned;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.handlers.PinnedQuests;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.utils.PacketHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

public record SyncPinnedQuestsPacket(Map<String, QuestProgress> quests) implements Packet<SyncPinnedQuestsPacket> {
    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "sync_pinned_quests");
    public static final PacketHandler<SyncPinnedQuestsPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<SyncPinnedQuestsPacket> getHandler() {
        return HANDLER;
    }

    public static class Handler implements PacketHandler<SyncPinnedQuestsPacket> {

        @Override
        public void encode(SyncPinnedQuestsPacket message, FriendlyByteBuf buffer) {
            buffer.writeVarInt(message.quests.size());
            for (var entry : message.quests.entrySet()) {
                buffer.writeUtf(entry.getKey());
                Quest quest = QuestHandler.get(entry.getKey());
                PacketHelper.writeWithYabn(Heracles.getRegistryAccess(), buffer, QuestProgress.codec(quest), entry.getValue(), true);
            }
        }

        @Override
        public SyncPinnedQuestsPacket decode(FriendlyByteBuf buffer) {
            Map<String, QuestProgress> quests = new LinkedHashMap<>();
            int size = buffer.readVarInt();
            for (int i = 0; i < size; i++) {
                String quest = buffer.readUtf();
                QuestProgress progress = PacketHelper.readWithYabn(Heracles.getRegistryAccess(), buffer, QuestProgress.codec(QuestHandler.get(quest)), true)
                    .getOrThrow(false, System.err::println);
                quests.put(quest, progress);
            }
            return new SyncPinnedQuestsPacket(quests);
        }

        @Override
        public PacketContext handle(SyncPinnedQuestsPacket message) {
            return (player, level) -> PinnedQuests.update(message.quests);
        }
    }
}
