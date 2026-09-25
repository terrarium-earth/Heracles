package earth.terrarium.heracles.api.rewards;

import com.mojang.serialization.MapCodec;
import earth.terrarium.heracles.api.client.settings.SettingsProvider;
import earth.terrarium.heracles.api.rewards.defaults.ItemReward;
import net.minecraft.resources.ResourceLocation;

public interface QuestRewardType<T extends QuestReward<T>> extends SettingsProvider<T> {

    ResourceLocation id();

    MapCodec<T> codec(String id);
}
