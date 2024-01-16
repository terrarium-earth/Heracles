package earth.terrarium.heracles.common.network.packets.quests;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.resources.ResourceLocation;

public record ClientboundAddQuestPacket(
    String id, Quest quest
) implements Packet<ClientboundAddQuestPacket> {

    public static final ClientboundPacketType<ClientboundAddQuestPacket> TYPE = new Type();

    @Override
    public PacketType<ClientboundAddQuestPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<ClientboundAddQuestPacket>, CodecPacketType<ClientboundAddQuestPacket> {

        private static final ByteCodec<ClientboundAddQuestPacket> CODEC = ObjectByteCodec.create(
            ByteCodec.STRING.fieldOf(ClientboundAddQuestPacket::id),
            ModUtils.toByteCodec(Quest.CODEC).fieldOf(ClientboundAddQuestPacket::quest),
            ClientboundAddQuestPacket::new
        );

        @Override
        public Class<ClientboundAddQuestPacket> type() {
            return ClientboundAddQuestPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "add_client_quest");
        }

        @Override
        public ByteCodec<ClientboundAddQuestPacket> codec() {
            return CODEC;
        }

        @Override
        public Runnable handle(ClientboundAddQuestPacket message) {
            return () -> ClientQuests.addQuest(message.id, message.quest);
        }
    }
}
