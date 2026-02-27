package earth.terrarium.heracles.client.ui.quests;

import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.BooleanSetting;
import earth.terrarium.heracles.api.client.settings.base.EnumSetting;
import earth.terrarium.heracles.api.quests.QuestDisplayStatus;
import earth.terrarium.heracles.api.quests.QuestSettings;
import org.jetbrains.annotations.Nullable;

public class QuestSettingsInitializer implements SettingInitializer<QuestSettings> {

    public static final QuestSettingsInitializer INSTANCE = new QuestSettingsInitializer();

    @Override
    public CreationData create(@Nullable QuestSettings object) {
        CreationData settings = new CreationData();
        settings.put("individual_progress", BooleanSetting.FALSE, object != null && object.individualProgress());
        settings.put("hidden", new EnumSetting<>(QuestDisplayStatus.class, QuestDisplayStatus.LOCKED), object != null ? object.hiddenUntil() : QuestDisplayStatus.LOCKED);
        settings.put("unlock_notification", BooleanSetting.FALSE, object != null && object.unlockNotification());
        settings.put("show_dependency_arrow", BooleanSetting.TRUE, object != null && object.showDependencyArrow());
        settings.put("repeatable", BooleanSetting.FALSE, object != null && object.repeatable());
        settings.put("auto_claim_rewards", BooleanSetting.FALSE, object != null && object.autoClaimRewards());
        return settings;
    }

    @Override
    public QuestSettings create(String id, QuestSettings object, Data data) {
        return new QuestSettings(
            data.get("individual_progress", BooleanSetting.FALSE).orElse(object != null && object.individualProgress()),
            data.get("hidden", new EnumSetting<>(QuestDisplayStatus.class, QuestDisplayStatus.LOCKED)).orElse(object != null ? object.hiddenUntil() : QuestDisplayStatus.LOCKED),
            data.get("unlock_notification", BooleanSetting.FALSE).orElse(object != null && object.unlockNotification()),
            data.get("show_dependency_arrow", BooleanSetting.TRUE).orElse(object != null && object.showDependencyArrow()),
            data.get("repeatable", BooleanSetting.FALSE).orElse(object != null && object.repeatable()),
            data.get("auto_claim_rewards", BooleanSetting.FALSE).orElse(object != null && object.autoClaimRewards())
        );
    }
}
