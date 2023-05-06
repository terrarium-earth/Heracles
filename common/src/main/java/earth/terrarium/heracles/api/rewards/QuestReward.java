package earth.terrarium.heracles.api.rewards;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.stream.Stream;

public interface QuestReward<T extends QuestReward<T>> {

    Stream<ItemStack> reward(ServerPlayer player);

    QuestRewardType<T> type();
}
