package earth.terrarium.heracles.api.client.settings.rewards;

import earth.terrarium.heracles.api.client.settings.CustomizableQuestElementSettings;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.AutocompleteTextSetting;
import earth.terrarium.heracles.api.rewards.defaults.LootTableReward;
import net.minecraft.Optionull;
import org.jetbrains.annotations.Nullable;

public class LootRewardSettings implements SettingInitializer<LootTableReward>, CustomizableQuestElementSettings<LootTableReward> {

    public static final LootRewardSettings INSTANCE = new LootRewardSettings();

    @Override
    public CreationData create(@Nullable LootTableReward object) {
        CreationData settings = CustomizableQuestElementSettings.super.create(object);
        settings.put("loottable", AutocompleteTextSetting.ALL_LOOT_TABLES, Optionull.map(object, LootTableReward::lootTable));
        return settings;
    }

    @Override
    public LootTableReward create(String id, @Nullable LootTableReward object, Data data) {
        return create(object, data, (title, icon) -> new LootTableReward(
            id,
            title,
            icon,
            data.get("loottable", AutocompleteTextSetting.ALL_LOOT_TABLES).orElse(null)
        ));
    }
}

