package earth.terrarium.heracles.common.network.packets.rewards;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Set;
import java.util.function.Consumer;

public record ClaimRewardsPacket(String quest, String reward) implements Packet<ClaimRewardsPacket> {

    public static final ServerboundPacketType<ClaimRewardsPacket> TYPE = new Type();

    public ClaimRewardsPacket(String quest) {
        this(quest, "");
    }

    @Override
    public PacketType<ClaimRewardsPacket> type() {
        return TYPE;
    }

    private static class Type implements ServerboundPacketType<ClaimRewardsPacket> {

        @Override
        public Class<ClaimRewardsPacket> type() {
            return ClaimRewardsPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "claim_rewards");
        }

        @Override
        public void encode(ClaimRewardsPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.quest);
            buffer.writeUtf(message.reward);
        }

        @Override
        public ClaimRewardsPacket decode(FriendlyByteBuf buffer) {
            return new ClaimRewardsPacket(buffer.readUtf(), buffer.readUtf());
        }

        @Override
        public Consumer<Player> handle(ClaimRewardsPacket message) {
            return (player) -> {
                Quest quest = QuestHandler.get(message.quest);
                if (quest != null) {
                    if (message.reward.isEmpty()) {
                        quest.claimAllowedRewards((ServerPlayer) player, message.quest);
                    } else {
                        quest.claimAllowedReward((ServerPlayer) player, message.quest, message.reward);
                    }
                    QuestProgressHandler.sync((ServerPlayer) player, Set.of(message.quest));
                }
            };
        }
    }
}
