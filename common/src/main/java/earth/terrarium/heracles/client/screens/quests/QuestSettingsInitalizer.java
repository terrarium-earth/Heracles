package earth.terrarium.heracles.client.screens.quests;

import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.BooleanSetting;
import earth.terrarium.heracles.api.client.settings.base.EnumSetting;
import earth.terrarium.heracles.api.quests.QuestSettings;
import earth.terrarium.heracles.common.utils.ModUtils;
import org.jetbrains.annotations.Nullable;

public class QuestSettingsInitalizer implements SettingInitializer<QuestSettings> {

    public static final QuestSettingsInitalizer INSTANCE = new QuestSettingsInitalizer();

    @Override
    public CreationData create(@Nullable QuestSettings object) {
        CreationData settings = new CreationData();
        settings.put("individual_progress", BooleanSetting.FALSE, object != null && object.individualProgress());
        settings.put("hidden", new EnumSetting<>(ModUtils.QuestStatus.class, ModUtils.QuestStatus.LOCKED), object != null ? object.hiddenUntil() : ModUtils.QuestStatus.LOCKED);
        settings.put("unlock_notification", BooleanSetting.FALSE, object != null && object.unlockNotification());
        settings.put("show_dependency_arrow", BooleanSetting.TRUE, object != null && object.showDependencyArrow());
        settings.put("repeatable", BooleanSetting.FALSE, object != null && object.repeatable());
        return settings;
    }

    @Override
    public QuestSettings create(String id, QuestSettings object, Data data) {
        return new QuestSettings(
            data.get("individual_progress", BooleanSetting.FALSE).orElse(object != null && object.individualProgress()),
            data.get("hidden", new EnumSetting<>(ModUtils.QuestStatus.class, ModUtils.QuestStatus.LOCKED)).orElse(object != null ? object.hiddenUntil() : ModUtils.QuestStatus.LOCKED),
            data.get("unlock_notification", BooleanSetting.FALSE).orElse(object != null && object.unlockNotification()),
            data.get("show_dependency_arrow", BooleanSetting.TRUE).orElse(object != null && object.showDependencyArrow()),
            data.get("repeatable", BooleanSetting.FALSE).orElse(object != null && object.repeatable())
        );
    }
}
