package earth.terrarium.heracles.common.network.packets;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record OpenGroupPacket(String group, boolean edit) implements Packet<OpenGroupPacket> {
    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "open_group");
    public static final PacketHandler<OpenGroupPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<OpenGroupPacket> getHandler() {
        return HANDLER;
    }

    public static class Handler implements PacketHandler<OpenGroupPacket> {

        @Override
        public void encode(OpenGroupPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.group);
            buffer.writeBoolean(message.edit);
        }

        @Override
        public OpenGroupPacket decode(FriendlyByteBuf buffer) {
            return new OpenGroupPacket(buffer.readUtf(), buffer.readBoolean());
        }

        @Override
        public PacketContext handle(OpenGroupPacket message) {
            return (player, level) -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    if (message.edit) {
                        ModUtils.editGroup(serverPlayer, message.group);
                    } else {
                        ModUtils.openGroup(serverPlayer, message.group);
                    }
                }
            };
        }
    }
}
