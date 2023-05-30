package earth.terrarium.heracles.common.network.packets.tasks;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.defaults.CheckTask;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record CheckTaskPacket(String id) implements Packet<CheckTaskPacket> {
    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "check_task");
    public static final PacketHandler<CheckTaskPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<CheckTaskPacket> getHandler() {
        return HANDLER;
    }

    public static class Handler implements PacketHandler<CheckTaskPacket> {

        @Override
        public void encode(CheckTaskPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.id);
        }

        @Override
        public CheckTaskPacket decode(FriendlyByteBuf buffer) {
            return new CheckTaskPacket(buffer.readUtf());
        }

        @Override
        public PacketContext handle(CheckTaskPacket message) {
            return (player, level) -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    QuestProgressHandler.getProgress(serverPlayer.getServer(), player.getUUID())
                        .testAndProgressTaskType(serverPlayer, message.id, CheckTask.TYPE);
                }
            };
        }
    }
}
