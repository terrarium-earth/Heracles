package earth.terrarium.heracles.api.rewards;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

public interface QuestRewardType<T extends QuestReward<T>> {

    ResourceLocation id();

    MapCodec<T> codec(String id);
}
