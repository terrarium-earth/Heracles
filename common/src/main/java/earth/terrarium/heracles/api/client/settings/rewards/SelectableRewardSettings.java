package earth.terrarium.heracles.api.client.settings.rewards;

import earth.terrarium.heracles.api.client.settings.CustomizableQuestElementSettings;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.IntSetting;
import earth.terrarium.heracles.api.client.settings.base.RewardsMapSetting;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.defaults.SelectableReward;
import net.minecraft.Optionull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class SelectableRewardSettings implements SettingInitializer<SelectableReward>, CustomizableQuestElementSettings<SelectableReward> {

    public static final SelectableRewardSettings INSTANCE = new SelectableRewardSettings();

    @Override
    public CreationData create(@Nullable SelectableReward object) {
        CreationData settings = CustomizableQuestElementSettings.super.create(object);
        settings.put("amount", IntSetting.ONE, getDefaultAmount(object));
        settings.put("rewards", RewardsMapSetting.INSTANCE, getDefaultRewards(object));
        return settings;
    }

    @Override
    public SelectableReward create(String id, @Nullable SelectableReward object, Data data) {
        return create(object, data, (title, icon) -> new SelectableReward(
            id,
            title,
            icon,
            data.get("amount", IntSetting.ONE).orElse(getDefaultAmount(object)),
            data.get("rewards", RewardsMapSetting.INSTANCE).orElse(getDefaultRewards(object))
        ));
    }

    private static int getDefaultAmount(@Nullable SelectableReward object) {
        return Optionull.mapOrDefault(object, SelectableReward::amount, 1);
    }

    private static Map<String, QuestReward<?>> getDefaultRewards(@Nullable SelectableReward object) {
        if (object != null && !object.rewards().isEmpty()) {
            return new LinkedHashMap<>(object.rewards());
        }
        return new LinkedHashMap<>();
    }
}
