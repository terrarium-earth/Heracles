package earth.terrarium.heracles.common.network.packets.quests;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.networking.base.CodecPacketHandler;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.common.network.packets.quests.data.NetworkQuestData;
import net.minecraft.resources.ResourceLocation;

public record ClientboundUpdateQuestPacket(
    String id, NetworkQuestData data
) implements Packet<ClientboundUpdateQuestPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "update_client_quest");
    public static final Handler HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<ClientboundUpdateQuestPacket> getHandler() {
        return HANDLER;
    }


    @SuppressWarnings("UnstableApiUsage")
    public static class Handler extends CodecPacketHandler<ClientboundUpdateQuestPacket> {

        public Handler() {
            super(ObjectByteCodec.create(
                ByteCodec.STRING.fieldOf(ClientboundUpdateQuestPacket::id),
                NetworkQuestData.CODEC.fieldOf(ClientboundUpdateQuestPacket::data),
                ClientboundUpdateQuestPacket::new
            ));
        }

        @Override
        public PacketContext handle(ClientboundUpdateQuestPacket message) {
            return (player, level) -> ClientQuests.get(message.id)
                .map(ClientQuests.QuestEntry::value)
                .ifPresent(message.data::update);
        }
    }
}
