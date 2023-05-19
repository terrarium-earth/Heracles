package earth.terrarium.heracles.api.client.settings.tasks;

import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.TextSetting;
import earth.terrarium.heracles.api.tasks.defaults.ChangedDimensionTask;
import net.minecraft.Optionull;
import org.jetbrains.annotations.Nullable;

public class DimensionTaskSettings implements SettingInitializer<ChangedDimensionTask> {

    public static final DimensionTaskSettings INSTANCE = new DimensionTaskSettings();

    @Override
    public CreationData create(@Nullable ChangedDimensionTask object) {
        CreationData settings = new CreationData();
        settings.put("from", TextSetting.DIMENSION, Optionull.map(object, ChangedDimensionTask::from));
        settings.put("to", TextSetting.DIMENSION, Optionull.map(object, ChangedDimensionTask::to));
        return settings;
    }

    @Override
    public ChangedDimensionTask create(String id, @Nullable ChangedDimensionTask object, Data data) {
        return new ChangedDimensionTask(
            id,
            data.get("from", TextSetting.DIMENSION).orElse(null),
            data.get("to", TextSetting.DIMENSION).orElse(null)
        );
    }
}
