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

public record ClaimRewardsPacket(String quest) implements Packet<ClaimRewardsPacket> {
    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "claim_rewards");
    public static final PacketHandler<ClaimRewardsPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<ClaimRewardsPacket> getHandler() {
        return HANDLER;
    }

    public static class Handler implements PacketHandler<ClaimRewardsPacket> {

        @Override
        public void encode(ClaimRewardsPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.quest);
        }

        @Override
        public ClaimRewardsPacket decode(FriendlyByteBuf buffer) {
            return new ClaimRewardsPacket(buffer.readUtf());
        }

        @Override
        public PacketContext handle(ClaimRewardsPacket message) {
            return (player, level) -> {
                Quest quest = QuestHandler.get(message.quest);
                if (quest != null) {
                    quest.claimAllowedRewards((ServerPlayer) player);
                }
            };
        }
    }
}
