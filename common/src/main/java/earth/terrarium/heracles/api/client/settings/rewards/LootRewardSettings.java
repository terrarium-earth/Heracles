package earth.terrarium.heracles.api.client.settings.rewards;

import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.TextSetting;
import earth.terrarium.heracles.api.rewards.defaults.LootTableReward;
import net.minecraft.Optionull;
import org.jetbrains.annotations.Nullable;

public class LootRewardSettings implements SettingInitializer<LootTableReward> {

    public static final LootRewardSettings INSTANCE = new LootRewardSettings();

    @Override
    public CreationData create(@Nullable LootTableReward object) {
        CreationData settings = new CreationData();
        settings.put("loottable", TextSetting.RESOURCELOCATION, Optionull.map(object, LootTableReward::lootTable));
        return settings;
    }

    @Override
    public LootTableReward create(String id, @Nullable LootTableReward object, Data data) {
        return new LootTableReward(
            id,
            data.get("loottable", TextSetting.RESOURCELOCATION).orElse(null)
        );
    }
}

