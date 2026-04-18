package earth.terrarium.heracles.common.network.packets.quests;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record SyncGroupOrderPacket(List<String> groupOrders) implements Packet<SyncGroupOrderPacket> {

    public static final ClientboundPacketType<SyncGroupOrderPacket> TYPE = new Type();

    @Override
    public PacketType<SyncGroupOrderPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<SyncGroupOrderPacket> {

        @Override
        public ResourceLocation id() {
            return Heracles.id("sync_group_order");
        }

        @Override
        public void encode(SyncGroupOrderPacket message, RegistryFriendlyByteBuf buffer) {
            buffer.writeCollection(message.groupOrders(), FriendlyByteBuf::writeUtf);
        }

        @Override
        public SyncGroupOrderPacket decode(RegistryFriendlyByteBuf buffer) {
            return new SyncGroupOrderPacket(buffer.readList(FriendlyByteBuf::readUtf));
        }

        @Override
        public Runnable handle(SyncGroupOrderPacket message) {
            return () -> ClientQuests.syncGroupOrders(message.groupOrders());
        }
    }
}
