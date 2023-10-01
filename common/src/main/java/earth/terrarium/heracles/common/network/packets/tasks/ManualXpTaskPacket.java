package earth.terrarium.heracles.common.network.packets.tasks;

import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.defaults.GatherItemTask;
import earth.terrarium.heracles.api.tasks.defaults.XpTask;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record ManualXpTaskPacket(String quest, String task) implements Packet<ManualXpTaskPacket> {
    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "check_xp");
    public static final PacketHandler<ManualXpTaskPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<ManualXpTaskPacket> getHandler() {
        return HANDLER;
    }

    public static class Handler implements PacketHandler<ManualXpTaskPacket> {

        @Override
        public void encode(ManualXpTaskPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.quest);
            buffer.writeUtf(message.task);
        }

        @Override
        public ManualXpTaskPacket decode(FriendlyByteBuf buffer) {
            return new ManualXpTaskPacket(buffer.readUtf(), buffer.readUtf());
        }

        @Override
        public PacketContext handle(ManualXpTaskPacket message) {
            return (player, level) -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    QuestProgressHandler.getProgress(serverPlayer.getServer(), player.getUUID())
                        .testAndProgressTask(serverPlayer, message.quest, message.task, Pair.of(player, XpTask.Cause.MANUALLY_COMPLETED), XpTask.TYPE);
                }
            };
        }
    }
}
