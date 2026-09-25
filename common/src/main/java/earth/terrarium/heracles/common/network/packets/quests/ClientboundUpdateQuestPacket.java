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

public record ClientboundUpdateQuestPacket(
    String id, NetworkQuestData data
) implements Packet<ClientboundUpdateQuestPacket> {

    public static final ClientboundPacketType<ClientboundUpdateQuestPacket> TYPE = new Type();

    @Override
    public PacketType<ClientboundUpdateQuestPacket> type() {
        return TYPE;
    }

    private static class Type extends CodecPacketType<ClientboundUpdateQuestPacket> implements ClientboundPacketType<ClientboundUpdateQuestPacket> {
        private static final ByteCodec<ClientboundUpdateQuestPacket> CODEC = ObjectByteCodec.create(
            ByteCodec.STRING.fieldOf(ClientboundUpdateQuestPacket::id),
            NetworkQuestData.CODEC.fieldOf(ClientboundUpdateQuestPacket::data),
            ClientboundUpdateQuestPacket::new
        );

        public Type() {
            super(Heracles.id("update_client_quest"), CODEC);
        }

        @Override
        public Runnable handle(ClientboundUpdateQuestPacket message) {
            return () -> ClientQuests.get(message.id)
                .map(ClientQuests.QuestEntry::value)
                .ifPresent(message.data::update);
        }
    }
}
