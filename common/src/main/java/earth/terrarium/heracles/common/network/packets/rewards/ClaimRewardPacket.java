package earth.terrarium.heracles.common.network.packets.rewards;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record ClaimRewardPacket(String quest, String reward) implements Packet<ClaimRewardPacket> {
    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "claim_reward");
    public static final PacketHandler<ClaimRewardPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<ClaimRewardPacket> getHandler() {
        return HANDLER;
    }

    public static class Handler implements PacketHandler<ClaimRewardPacket> {

        @Override
        public void encode(ClaimRewardPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.quest);
            buffer.writeUtf(message.reward);
        }

        @Override
        public ClaimRewardPacket decode(FriendlyByteBuf buffer) {
            return new ClaimRewardPacket(buffer.readUtf(), buffer.readUtf());
        }

        @Override
        public PacketContext handle(ClaimRewardPacket message) {
            return (player, level) -> {
                Quest quest = QuestHandler.get(message.quest);
                if (quest != null) {
                    quest.claimAllowedReward((ServerPlayer) player, message.quest, message.reward);
                }
            };
        }
    }
}
