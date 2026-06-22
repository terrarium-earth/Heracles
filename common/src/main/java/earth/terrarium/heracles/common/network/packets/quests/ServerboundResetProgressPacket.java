package earth.terrarium.heracles.common.network.packets.quests;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.progress.QuestsProgress;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Set;
import java.util.function.Consumer;

public record ServerboundResetProgressPacket(String quest, String entryId, boolean isTask) implements Packet<ServerboundResetProgressPacket> {

    public static final ServerboundPacketType<ServerboundResetProgressPacket> TYPE = new Type();

    @Override
    public PacketType<ServerboundResetProgressPacket> type() {
        return TYPE;
    }

    private static class Type implements ServerboundPacketType<ServerboundResetProgressPacket> {

        @Override
        public Class<ServerboundResetProgressPacket> type() {
            return ServerboundResetProgressPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return Heracles.id("reset_progress");
        }

        @Override
        public void encode(ServerboundResetProgressPacket message, RegistryFriendlyByteBuf buffer) {
            buffer.writeUtf(message.quest);
            buffer.writeUtf(message.entryId);
            buffer.writeBoolean(message.isTask);
        }

        @Override
        public ServerboundResetProgressPacket decode(RegistryFriendlyByteBuf buffer) {
            return new ServerboundResetProgressPacket(buffer.readUtf(), buffer.readUtf(), buffer.readBoolean());
        }

        @Override
        public Consumer<Player> handle(ServerboundResetProgressPacket message) {
            return (player) -> {
                if (!player.hasPermissions(2)) return;
                ServerPlayer serverPlayer = (ServerPlayer) player;
                Quest quest = QuestHandler.get(message.quest);
                if (quest == null) return;

                QuestsProgress questsProgress = QuestProgressHandler.getProgress(serverPlayer.server, serverPlayer.getUUID());
                QuestProgress progress = questsProgress.getProgress(message.quest);

                if (message.isTask) {
                    var task = quest.tasks().get(message.entryId);
                    if (task == null) return;
                    TaskProgress<?> taskProgress = progress.getTask(task);
                    taskProgress.reset();
                    progress.setComplete(false);
                } else {
                    progress.claimedRewards().remove(message.entryId);
                }

                QuestProgressHandler.sync(serverPlayer, Set.of(message.quest));
            };
        }
    }
}
