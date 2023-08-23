package earth.terrarium.heracles.client.screens.quests;

import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.BooleanSetting;
import earth.terrarium.heracles.api.quests.QuestSettings;
import org.jetbrains.annotations.Nullable;

public class QuestSettingsInitalizer implements SettingInitializer<QuestSettings> {

    public static final QuestSettingsInitalizer INSTANCE = new QuestSettingsInitalizer();

    @Override
    public CreationData create(@Nullable QuestSettings object) {
        CreationData settings = new CreationData();
        settings.put("individual_progress", BooleanSetting.FALSE, object != null && object.individualProgress());
        settings.put("hidden", BooleanSetting.FALSE, object != null && object.hidden());
        settings.put("unlock_notification", BooleanSetting.FALSE, object != null && object.unlockNotification());
        return settings;
    }

    @Override
    public QuestSettings create(String id, QuestSettings object, Data data) {
        return new QuestSettings(
            data.get("individual_progress", BooleanSetting.FALSE).orElse(object != null && object.individualProgress()),
            data.get("hidden", BooleanSetting.FALSE).orElse(object != null && object.hidden()),
            data.get("unlock_notification", BooleanSetting.FALSE).orElse(object != null && object.unlockNotification())
        );
    }
}
