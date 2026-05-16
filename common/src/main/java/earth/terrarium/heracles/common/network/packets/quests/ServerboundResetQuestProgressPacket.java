package earth.terrarium.heracles.common.network.packets.quests;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.progress.QuestsProgress;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Set;
import java.util.function.Consumer;

public record ServerboundResetQuestProgressPacket(String quest) implements Packet<ServerboundResetQuestProgressPacket> {

    public static final ServerboundPacketType<ServerboundResetQuestProgressPacket> TYPE = new Type();

    @Override
    public PacketType<ServerboundResetQuestProgressPacket> type() {
        return TYPE;
    }

    private static class Type implements ServerboundPacketType<ServerboundResetQuestProgressPacket> {

        @Override
        public Class<ServerboundResetQuestProgressPacket> type() {
            return ServerboundResetQuestProgressPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return Heracles.id("reset_quest_progress");
        }

        @Override
        public void encode(ServerboundResetQuestProgressPacket message, RegistryFriendlyByteBuf buffer) {
            buffer.writeUtf(message.quest);
        }

        @Override
        public ServerboundResetQuestProgressPacket decode(RegistryFriendlyByteBuf buffer) {
            return new ServerboundResetQuestProgressPacket(buffer.readUtf());
        }

        @Override
        public Consumer<Player> handle(ServerboundResetQuestProgressPacket message) {
            return (player) -> {
                if (!player.hasPermissions(2)) return;
                ServerPlayer serverPlayer = (ServerPlayer) player;
                Quest quest = QuestHandler.get(message.quest);
                if (quest == null) return;

                QuestsProgress questsProgress = QuestProgressHandler.getProgress(serverPlayer.server, serverPlayer.getUUID());
                QuestProgress progress = questsProgress.getProgress(message.quest);
                progress.reset();

                QuestProgressHandler.sync(serverPlayer, Set.of(message.quest));
            };
        }
    }
}
