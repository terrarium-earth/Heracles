package earth.terrarium.heracles.api.rewards;

import com.mojang.serialization.Codec;
import earth.terrarium.heracles.api.client.settings.SettingsProvider;
import net.minecraft.resources.ResourceLocation;

public interface QuestRewardType<T extends QuestReward<T>> extends SettingsProvider<T> {

    ResourceLocation id();

    Codec<T> codec(String id);
}
