package earth.terrarium.heracles.common.network.packets.quests;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ClientboundRemoveQuestPacket(String id) implements Packet<ClientboundRemoveQuestPacket> {

    public static final ClientboundPacketType<ClientboundRemoveQuestPacket> TYPE = new Type();

    @Override
    public PacketType<ClientboundRemoveQuestPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<ClientboundRemoveQuestPacket> {

        @Override
        public Class<ClientboundRemoveQuestPacket> type() {
            return ClientboundRemoveQuestPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "remove_client_quest");
        }

        @Override
        public void encode(ClientboundRemoveQuestPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.id);
        }

        @Override
        public ClientboundRemoveQuestPacket decode(FriendlyByteBuf buffer) {
            return new ClientboundRemoveQuestPacket(buffer.readUtf());
        }

        @Override
        public Runnable handle(ClientboundRemoveQuestPacket message) {
            return () -> ClientQuests.remove(message.id);
        }
    }
}
