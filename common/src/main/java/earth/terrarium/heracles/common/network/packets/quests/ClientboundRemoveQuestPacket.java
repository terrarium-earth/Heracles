package earth.terrarium.heracles.common.network.packets.quests;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.resourcefullib.common.networking.base.CodecPacketHandler;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import net.minecraft.resources.ResourceLocation;

public record ClientboundRemoveQuestPacket(String id) implements Packet<ClientboundRemoveQuestPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "remove_client_quest");
    public static final Handler HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<ClientboundRemoveQuestPacket> getHandler() {
        return HANDLER;
    }


    @SuppressWarnings("UnstableApiUsage")
    public static class Handler extends CodecPacketHandler<ClientboundRemoveQuestPacket> {

        public Handler() {
            super(ByteCodec.STRING.map(ClientboundRemoveQuestPacket::new, ClientboundRemoveQuestPacket::id));
        }

        @Override
        public PacketContext handle(ClientboundRemoveQuestPacket message) {
            return (player, level) -> ClientQuests.remove(message.id);
        }
    }
}
