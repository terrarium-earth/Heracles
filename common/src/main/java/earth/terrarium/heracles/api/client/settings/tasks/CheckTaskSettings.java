package earth.terrarium.heracles.api.client.settings.tasks;

import earth.terrarium.heracles.api.client.settings.CustomizableQuestElementSettings;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.tasks.defaults.CheckTask;
import org.jetbrains.annotations.Nullable;

public class CheckTaskSettings implements SettingInitializer<CheckTask>, CustomizableQuestElementSettings<CheckTask> {

    public static final CheckTaskSettings INSTANCE = new CheckTaskSettings();

    @Override
    public CreationData create(@Nullable CheckTask object) {
        return CustomizableQuestElementSettings.super.create(object);
    }

    @Override
    public CheckTask create(String id, CheckTask object, Data data) {
        return create(object, data, (title, icon) -> new CheckTask(
            id,
            title,
            icon
        ));
    }
}
