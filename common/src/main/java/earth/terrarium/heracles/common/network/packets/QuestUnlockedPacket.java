package earth.terrarium.heracles.common.network.packets;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.HeraclesClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record QuestUnlockedPacket(String id) implements Packet<QuestUnlockedPacket> {
    public static final ClientboundPacketType<QuestUnlockedPacket> TYPE = new Type();

    @Override
    public PacketType<QuestUnlockedPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<QuestUnlockedPacket> {
        @Override
        public Class<QuestUnlockedPacket> type() {
            return QuestUnlockedPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "quest_unlocked");
        }

        @Override
        public void encode(QuestUnlockedPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.id);
        }

        @Override
        public QuestUnlockedPacket decode(FriendlyByteBuf buffer) {
            return new QuestUnlockedPacket(buffer.readUtf());
        }

        @Override
        public Runnable handle(QuestUnlockedPacket message) {
            return () -> HeraclesClient.displayQuestUnlockedToast(message.id());
        }
    }
}
