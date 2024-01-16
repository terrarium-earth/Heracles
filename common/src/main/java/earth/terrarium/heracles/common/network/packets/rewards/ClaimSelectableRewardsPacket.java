package earth.terrarium.heracles.common.network.packets.rewards;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.defaults.SelectableReward;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.progress.QuestsProgress;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public record ClaimSelectableRewardsPacket(
    String quest, String reward, Collection<String> rewards
) implements Packet<ClaimSelectableRewardsPacket> {

    public static final ServerboundPacketType<ClaimSelectableRewardsPacket> TYPE = new Type();

    @Override
    public PacketType<ClaimSelectableRewardsPacket> type() {
        return TYPE;
    }

    private static class Type implements ServerboundPacketType<ClaimSelectableRewardsPacket> {

        @Override
        public Class<ClaimSelectableRewardsPacket> type() {
            return ClaimSelectableRewardsPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "claim_selectable_rewards");
        }

        @Override
        public void encode(ClaimSelectableRewardsPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.quest);
            buffer.writeUtf(message.reward);
            buffer.writeCollection(message.rewards, FriendlyByteBuf::writeUtf);
        }

        @Override
        public ClaimSelectableRewardsPacket decode(FriendlyByteBuf buffer) {
            return new ClaimSelectableRewardsPacket(
                buffer.readUtf(),
                buffer.readUtf(),
                buffer.readCollection(ArrayList::new, FriendlyByteBuf::readUtf)
            );
        }

        @Override
        public Consumer<Player> handle(ClaimSelectableRewardsPacket message) {
            return (player) -> {
                ServerPlayer serverPlayer = (ServerPlayer) player;
                Quest quest = QuestHandler.get(message.quest);
                if (quest != null) {
                    QuestsProgress progress = QuestProgressHandler.getProgress(serverPlayer.getServer(), player.getUUID());
                    QuestProgress questProgress = progress.getProgress(message.quest);
                    if (questProgress.canClaim(message.reward)) {
                        questProgress.claimReward(message.reward);
                        QuestReward<?> reward = quest.rewards().get(message.reward);
                        if (reward instanceof SelectableReward selectableReward) {
                            if (message.rewards().size() <= selectableReward.amount()) {
                                quest.claimRewards(
                                    message.quest,
                                    serverPlayer,
                                    message.rewards().stream()
                                        .map(selectableReward.rewards()::get)
                                        .filter(Objects::nonNull)
                                );
                                QuestProgressHandler.sync((ServerPlayer) player, Set.of(message.quest));
                            }
                        }
                    }
                }
            };
        }
    }
}
