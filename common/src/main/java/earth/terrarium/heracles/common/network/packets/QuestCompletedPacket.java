package earth.terrarium.heracles.common.network.packets;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.HeraclesClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record QuestCompletedPacket(String id) implements Packet<QuestCompletedPacket> {
    public static final ClientboundPacketType<QuestCompletedPacket> TYPE = new Type();

    @Override
    public PacketType<QuestCompletedPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<QuestCompletedPacket> {
        @Override
        public Class<QuestCompletedPacket> type() {
            return QuestCompletedPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "quest_complete");
        }

        @Override
        public void encode(QuestCompletedPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.id);
        }

        @Override
        public QuestCompletedPacket decode(FriendlyByteBuf buffer) {
            return new QuestCompletedPacket(buffer.readUtf());
        }

        @Override
        public Runnable handle(QuestCompletedPacket message) {
            return () -> HeraclesClient.displayQuestCompleteToast(message.id());
        }
    }
}
