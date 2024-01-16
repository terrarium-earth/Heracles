package earth.terrarium.heracles.common.network.packets.tasks;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import earth.terrarium.heracles.api.tasks.defaults.CheckTask;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Set;
import java.util.function.Consumer;

public record CheckTaskPacket(String quest, String task) implements Packet<CheckTaskPacket> {
    public static final ServerboundPacketType<CheckTaskPacket> TYPE = new Type();

    @Override
    public PacketType<CheckTaskPacket> type() {
        return TYPE;
    }

    private static class Type implements ServerboundPacketType<CheckTaskPacket> {

        @Override
        public Class<CheckTaskPacket> type() {
            return CheckTaskPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation("heracles", "check_task");
        }

        @Override
        public void encode(CheckTaskPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.quest);
            buffer.writeUtf(message.task);
        }

        @Override
        public CheckTaskPacket decode(FriendlyByteBuf buffer) {
            return new CheckTaskPacket(buffer.readUtf(), buffer.readUtf());
        }

        @Override
        public Consumer<Player> handle(CheckTaskPacket message) {
            return (player) -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    QuestProgressHandler.getProgress(serverPlayer.getServer(), player.getUUID())
                        .testAndProgressTask(serverPlayer, message.quest, message.task, serverPlayer, CheckTask.TYPE);
                    QuestProgressHandler.sync(serverPlayer, Set.of(message.quest));
                }
            };
        }
    }
}
