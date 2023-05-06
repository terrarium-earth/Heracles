package earth.terrarium.heracles.api.rewards;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.stream.Stream;

public interface QuestReward<T extends QuestReward<T>> {

    /**
     * The id of the reward.
     *
     * @return The id.
     */
    String id();

    /**
     * The method that is called when the reward is claimed.
     *
     * @return The items that are given to the player, if any.
     * <br>
     * Empty stream if none.
     * <br>
     * This is only for display purposes.
     */
    Stream<ItemStack> reward(ServerPlayer player);

    /**
     * The type of the reward.
     *
     * @return The type.
     */
    QuestRewardType<T> type();
}
