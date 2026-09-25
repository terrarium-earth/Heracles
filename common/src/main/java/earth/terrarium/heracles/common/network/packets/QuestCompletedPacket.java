package earth.terrarium.heracles.common.network.packets;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.HeraclesClient;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record QuestCompletedPacket(String id) implements Packet<QuestCompletedPacket> {
    public static final ClientboundPacketType<QuestCompletedPacket> TYPE = new Type();

    @Override
    public PacketType<QuestCompletedPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<QuestCompletedPacket> {
        @Override
        public ResourceLocation id() {
            return Heracles.id("quest_complete");
        }

        @Override
        public void encode(QuestCompletedPacket message, RegistryFriendlyByteBuf buffer) {
            buffer.writeUtf(message.id);
        }

        @Override
        public QuestCompletedPacket decode(RegistryFriendlyByteBuf buffer) {
            return new QuestCompletedPacket(buffer.readUtf());
        }

        @Override
        public Runnable handle(QuestCompletedPacket message) {
            return () -> HeraclesClient.displayQuestCompleteToast(message.id());
        }
    }
}
