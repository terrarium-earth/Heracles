package earth.terrarium.heracles.api.client.settings.rewards;

import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.EnumSetting;
import earth.terrarium.heracles.api.client.settings.base.IntSetting;
import earth.terrarium.heracles.api.rewards.defaults.XpQuestReward;
import net.minecraft.Optionull;
import org.jetbrains.annotations.Nullable;

public class XpRewardSettings implements SettingInitializer<XpQuestReward> {

    private static final EnumSetting<XpQuestReward.XpType> TYPE = new EnumSetting<>(XpQuestReward.XpType.class, XpQuestReward.XpType.LEVEL);

    public static final XpRewardSettings INSTANCE = new XpRewardSettings();

    @Override
    public CreationData create(@Nullable XpQuestReward object) {
        CreationData settings = new CreationData();
        settings.put("type", TYPE, Optionull.map(object, XpQuestReward::xpType));
        settings.put("amount", IntSetting.ONE, Optionull.map(object, XpQuestReward::amount));
        return settings;
    }

    @Override
    public XpQuestReward create(String id, @Nullable XpQuestReward object, Data data) {
        return new XpQuestReward(
            id,
            data.get("type", TYPE).orElse(Optionull.mapOrDefault(object, XpQuestReward::xpType, XpQuestReward.XpType.LEVEL)),
            data.get("amount", IntSetting.ONE).orElse(Optionull.mapOrDefault(object, XpQuestReward::amount, 1))
        );
    }
}

