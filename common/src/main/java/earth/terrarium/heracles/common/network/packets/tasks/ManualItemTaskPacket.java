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
import net.minecraft.world.item.ItemStack;

public record ManualItemTaskPacket(String quest, String task) implements Packet<ManualItemTaskPacket> {
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
            buffer.writeUtf(message.quest);
            buffer.writeUtf(message.task);
        }

        @Override
        public ManualItemTaskPacket decode(FriendlyByteBuf buffer) {
            return new ManualItemTaskPacket(buffer.readUtf(), buffer.readUtf());
        }

        @Override
        public PacketContext handle(ManualItemTaskPacket message) {
            return (player, level) -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    QuestProgressHandler.getProgress(serverPlayer.getServer(), player.getUUID())
                        .testAndProgressTask(serverPlayer, message.quest, message.task, Pair.of(ItemStack.EMPTY, player.getInventory()), GatherItemTask.TYPE);
                }
            };
        }
    }
}
