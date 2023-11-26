package earth.terrarium.heracles.common.network.packets.pinned;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.handlers.PinnedQuests;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

public record SyncPinnedQuestsPacket(Map<String, QuestProgress> quests) implements Packet<SyncPinnedQuestsPacket> {

    public static final ClientboundPacketType<SyncPinnedQuestsPacket> TYPE = new Type();

    @Override
    public PacketType<SyncPinnedQuestsPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<SyncPinnedQuestsPacket> {

        @Override
        public Class<SyncPinnedQuestsPacket> type() {
            return SyncPinnedQuestsPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "sync_pinned_quests");
        }

        @Override
        public void encode(SyncPinnedQuestsPacket message, FriendlyByteBuf buffer) {
            buffer.writeVarInt(message.quests.size());
            for (var entry : message.quests.entrySet()) {
                buffer.writeUtf(entry.getKey());
                buffer.writeNbt(entry.getValue().save());
            }
        }

        @Override
        public SyncPinnedQuestsPacket decode(FriendlyByteBuf buffer) {
            Map<String, QuestProgress> quests = new LinkedHashMap<>();
            int size = buffer.readVarInt();
            for (int i = 0; i < size; i++) {
                String quest = buffer.readUtf();
                ClientQuests.get(quest).ifPresent(entry -> {
                    QuestProgress progress = new QuestProgress(entry.value(), buffer.readNbt());
                    quests.put(quest, progress);
                });
            }
            return new SyncPinnedQuestsPacket(quests);
        }

        @Override
        public Runnable handle(SyncPinnedQuestsPacket message) {
            return () -> PinnedQuests.update(message.quests);
        }
    }
}
