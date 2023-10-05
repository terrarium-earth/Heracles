package earth.terrarium.heracles.api.client.settings.tasks;

import earth.terrarium.heracles.api.client.settings.CustomizableQuestElementSettings;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.RegistryKeySetting;
import earth.terrarium.heracles.api.tasks.defaults.ChangedDimensionTask;
import net.minecraft.Optionull;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class DimensionTaskSettings implements SettingInitializer<ChangedDimensionTask>, CustomizableQuestElementSettings<ChangedDimensionTask> {

    public static final DimensionTaskSettings INSTANCE = new DimensionTaskSettings();

    @Override
    public CreationData create(@Nullable ChangedDimensionTask object) {
        CreationData settings = CustomizableQuestElementSettings.super.create(object);
        settings.put("from", RegistryKeySetting.DIMENSION, Optionull.mapOrDefault(object, ChangedDimensionTask::from, Level.OVERWORLD));
        settings.put("to", RegistryKeySetting.DIMENSION, Optionull.mapOrDefault(object, ChangedDimensionTask::to, Level.OVERWORLD));
        return settings;
    }

    @Override
    public ChangedDimensionTask create(String id, @Nullable ChangedDimensionTask object, Data data) {
        return create(object, data, (title, icon) -> new ChangedDimensionTask(
            id,
            title,
            icon,
            data.get("from", RegistryKeySetting.DIMENSION).orElse(Optionull.map(object, ChangedDimensionTask::from)),
            data.get("to", RegistryKeySetting.DIMENSION).orElse(Optionull.map(object, ChangedDimensionTask::to))
        ));
    }
}
