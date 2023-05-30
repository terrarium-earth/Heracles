package earth.terrarium.heracles.api.client.settings.tasks;

import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.TextSetting;
import earth.terrarium.heracles.api.tasks.defaults.CheckTask;
import net.minecraft.Optionull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CheckTaskSettings implements SettingInitializer<CheckTask> {

    public static final CheckTaskSettings INSTANCE = new CheckTaskSettings();

    @Override
    public CreationData create(@Nullable CheckTask object) {
        CreationData settings = new CreationData();
        settings.put("id", TextSetting.INSTANCE, getDefaultId(object));
        return settings;
    }

    @Override
    public CheckTask create(String id, CheckTask object, Data data) {
        return new CheckTask(id, data.get("id", TextSetting.INSTANCE).orElse(getDefaultId(object)));
    }

    private static String getDefaultId(CheckTask object) {
        return Optionull.mapOrDefault(object, CheckTask::checkId, UUID.randomUUID().toString());
    }
}
