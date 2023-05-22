package earth.terrarium.heracles.common.network.packets.quests;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record SyncDescriptionsPacket(Map<String, String> descriptions) implements Packet<SyncDescriptionsPacket> {
    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "sync_descriptions");
    public static final PacketHandler<SyncDescriptionsPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<SyncDescriptionsPacket> getHandler() {
        return HANDLER;
    }

    public static class Handler implements PacketHandler<SyncDescriptionsPacket> {

        @Override
        public void encode(SyncDescriptionsPacket message, FriendlyByteBuf buffer) {
            buffer.writeMap(message.descriptions(), FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeUtf);
        }

        @Override
        public SyncDescriptionsPacket decode(FriendlyByteBuf buffer) {
            return new SyncDescriptionsPacket(buffer.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readUtf));
        }

        @Override
        public PacketContext handle(SyncDescriptionsPacket message) {
            return (player, level) -> ClientQuests.syncDescriptions(message.descriptions());
        }
    }
}
