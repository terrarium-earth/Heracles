package earth.terrarium.hercules;

import earth.terrarium.hercules.condition.QuestCondition;
import earth.terrarium.hercules.reward.QuestReward;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerPlayer;

public record Quest(Holder<QuestCondition> condition, HolderSet<QuestReward> rewards) {
    public void reward(ServerPlayer player) {
        for (Holder<QuestReward> reward : rewards()) {
            reward.value().reward(player);
        }
    }
}
