package earth.terrarium.heracles.common.network.packets.tasks;

import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.defaults.GatherItemTask;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;
import java.util.function.Consumer;

public record ManualItemTaskPacket(String quest, String task) implements Packet<ManualItemTaskPacket> {
    public static final ServerboundPacketType<ManualItemTaskPacket> TYPE = new Type();

    @Override
    public PacketType<ManualItemTaskPacket> type() {
        return TYPE;
    }

    private static class Type implements ServerboundPacketType<ManualItemTaskPacket> {

        @Override
        public Class<ManualItemTaskPacket> type() {
            return ManualItemTaskPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "check_item");
        }

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
        public Consumer<Player> handle(ManualItemTaskPacket message) {
            return (player) -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    QuestProgressHandler.getProgress(serverPlayer.getServer(), player.getUUID())
                        .testAndProgressTask(serverPlayer, message.quest, message.task, Pair.of(Optional.empty(), player.getInventory()), GatherItemTask.TYPE);
                }
            };
        }
    }
}
