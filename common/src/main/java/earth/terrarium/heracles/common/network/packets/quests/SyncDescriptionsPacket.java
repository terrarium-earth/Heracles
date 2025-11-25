package earth.terrarium.heracles.common.network.packets.quests;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record SyncDescriptionsPacket(Map<String, String> descriptions) implements Packet<SyncDescriptionsPacket> {

    public static final ClientboundPacketType<SyncDescriptionsPacket> TYPE = new Type();

    @Override
    public PacketType<SyncDescriptionsPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<SyncDescriptionsPacket> {

        @Override
        public ResourceLocation id() {
            return ResourceLocation.fromNamespaceAndPath(Heracles.MOD_ID, "sync_descriptions");
        }

        @Override
        public void encode(SyncDescriptionsPacket message, RegistryFriendlyByteBuf buffer) {
            buffer.writeMap(message.descriptions(), (buf, key) -> buf.writeUtf(key), (buf, value) -> buf.writeUtf(value));
        }

        @Override
        public SyncDescriptionsPacket decode(RegistryFriendlyByteBuf buffer) {
            return new SyncDescriptionsPacket(buffer.readMap(buf -> buf.readUtf(), buf -> buf.readUtf()));
        }

        @Override
        public Runnable handle(SyncDescriptionsPacket message) {
            return () -> ClientQuests.syncDescriptions(message.descriptions());
        }
    }
}
