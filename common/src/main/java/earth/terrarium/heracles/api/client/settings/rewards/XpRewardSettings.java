package earth.terrarium.heracles.api.client.settings.rewards;

import earth.terrarium.heracles.api.client.settings.CustomizableQuestElementSettings;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.EnumSetting;
import earth.terrarium.heracles.api.client.settings.base.IntSetting;
import earth.terrarium.heracles.api.rewards.defaults.XpQuestReward;
import net.minecraft.Optionull;
import org.jetbrains.annotations.Nullable;

public class XpRewardSettings implements SettingInitializer<XpQuestReward>, CustomizableQuestElementSettings<XpQuestReward> {

    private static final EnumSetting<XpQuestReward.XpType> TYPE = new EnumSetting<>(XpQuestReward.XpType.class, XpQuestReward.XpType.LEVEL);

    public static final XpRewardSettings INSTANCE = new XpRewardSettings();

    @Override
    public CreationData create(@Nullable XpQuestReward object) {
        CreationData settings = CustomizableQuestElementSettings.super.create(object);
        settings.put("type", TYPE, getDefaultType(object));
        settings.put("amount", IntSetting.ONE, getDefaultAmount(object));
        return settings;
    }

    @Override
    public XpQuestReward create(String id, @Nullable XpQuestReward object, Data data) {
        return create(object, data, (title, icon) -> new XpQuestReward(
            id,
            title,
            icon,
            data.get("type", TYPE).orElse(getDefaultType(object)),
            data.get("amount", IntSetting.ONE).orElse(getDefaultAmount(object))
        ));
    }

    private static XpQuestReward.XpType getDefaultType(XpQuestReward object) {
        return Optionull.mapOrDefault(object, XpQuestReward::xpType, XpQuestReward.XpType.LEVEL);
    }

    private static int getDefaultAmount(XpQuestReward object) {
        return Optionull.mapOrDefault(object, XpQuestReward::amount, 1);
    }
}

