package earth.terrarium.heracles.common.network.packets.tasks;

import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.defaults.XpTask;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record ManualXpTaskPacket(String quest, String task) implements Packet<ManualXpTaskPacket> {
    public static final ServerboundPacketType<ManualXpTaskPacket> TYPE = new Type();

    @Override
    public PacketType<ManualXpTaskPacket> type() {
        return TYPE;
    }

    private static class Type implements ServerboundPacketType<ManualXpTaskPacket> {

        @Override
        public Class<ManualXpTaskPacket> type() {
            return ManualXpTaskPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "check_xp");
        }

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
        public Consumer<Player> handle(ManualXpTaskPacket message) {
            return (player) -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    QuestProgressHandler.getProgress(serverPlayer.getServer(), player.getUUID())
                        .testAndProgressTask(serverPlayer, message.quest, message.task, Pair.of(player, XpTask.Cause.MANUALLY_COMPLETED), XpTask.TYPE);
                }
            };
        }
    }
}
