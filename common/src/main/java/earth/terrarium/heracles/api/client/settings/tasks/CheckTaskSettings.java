package earth.terrarium.heracles.api.client.settings.tasks;

import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.tasks.defaults.CheckTask;
import org.jetbrains.annotations.Nullable;

public class CheckTaskSettings implements SettingInitializer<CheckTask> {

    public static final CheckTaskSettings INSTANCE = new CheckTaskSettings();

    @Override
    public CreationData create(@Nullable CheckTask object) {
        return new CreationData();
    }

    @Override
    public CheckTask create(String id, CheckTask object, Data data) {
        return new CheckTask(id);
    }
}
