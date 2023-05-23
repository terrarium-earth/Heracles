package earth.terrarium.heracles.api.client.settings.tasks;

import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.RegistryValueSetting;
import earth.terrarium.heracles.api.tasks.defaults.StructureTask;
import net.minecraft.Optionull;
import org.jetbrains.annotations.Nullable;

public class StructureTaskSettings implements SettingInitializer<StructureTask> {

    public static final StructureTaskSettings INSTANCE = new StructureTaskSettings();

    @Override
    public CreationData create(@Nullable StructureTask object) {
        CreationData settings = new CreationData();
        settings.put("structure", RegistryValueSetting.STRUCTURE, Optionull.map(object, StructureTask::structures));
        return settings;
    }

    @Override
    public StructureTask create(String id, @Nullable StructureTask object, Data data) {
        return new StructureTask(
            id,
            data.get("structure", RegistryValueSetting.STRUCTURE).orElse(Optionull.mapOrDefault(object, StructureTask::structures, null))
        );
    }
}
