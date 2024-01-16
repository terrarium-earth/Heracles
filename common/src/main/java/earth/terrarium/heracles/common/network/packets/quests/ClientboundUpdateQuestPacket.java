package earth.terrarium.heracles.common.network.packets.quests;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.common.network.packets.quests.data.NetworkQuestData;
import net.minecraft.resources.ResourceLocation;

public record ClientboundUpdateQuestPacket(
    String id, NetworkQuestData data
) implements Packet<ClientboundUpdateQuestPacket> {

    public static final ClientboundPacketType<ClientboundUpdateQuestPacket> TYPE = new Type();

    @Override
    public PacketType<ClientboundUpdateQuestPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<ClientboundUpdateQuestPacket>, CodecPacketType<ClientboundUpdateQuestPacket> {
        private static final ByteCodec<ClientboundUpdateQuestPacket> CODEC = ObjectByteCodec.create(
            ByteCodec.STRING.fieldOf(ClientboundUpdateQuestPacket::id),
            NetworkQuestData.CODEC.fieldOf(ClientboundUpdateQuestPacket::data),
            ClientboundUpdateQuestPacket::new
        );

        @Override
        public Class<ClientboundUpdateQuestPacket> type() {
            return ClientboundUpdateQuestPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "update_client_quest");
        }

        @Override
        public ByteCodec<ClientboundUpdateQuestPacket> codec() {
            return CODEC;
        }

        @Override
        public Runnable handle(ClientboundUpdateQuestPacket message) {
            return () -> ClientQuests.get(message.id)
                .map(ClientQuests.QuestEntry::value)
                .ifPresent(message.data::update);
        }
    }
}
