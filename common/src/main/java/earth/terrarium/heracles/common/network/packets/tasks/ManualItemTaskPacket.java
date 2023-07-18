package earth.terrarium.heracles.common.network.packets.tasks;

import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.defaults.GatherItemTask;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record ManualItemTaskPacket(String task) implements Packet<ManualItemTaskPacket> {
    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "check_item");
    public static final PacketHandler<ManualItemTaskPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<ManualItemTaskPacket> getHandler() {
        return HANDLER;
    }

    public static class Handler implements PacketHandler<ManualItemTaskPacket> {

        @Override
        public void encode(ManualItemTaskPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.task);
        }

        @Override
        public ManualItemTaskPacket decode(FriendlyByteBuf buffer) {
            return new ManualItemTaskPacket(buffer.readUtf());
        }

        @Override
        public PacketContext handle(ManualItemTaskPacket message) {
            return (player, level) -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    QuestProgressHandler.getProgress(serverPlayer.getServer(), player.getUUID())
                            .testAndProgressTaskType(serverPlayer, Pair.of(message.task, player.getInventory()), GatherItemTask.TYPE);
                }
            };
        }
    }
}
