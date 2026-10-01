package earth.terrarium.heracles.api.client.settings.tasks;

import earth.terrarium.heracles.api.client.settings.CustomizableQuestElementSettings;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.BooleanSetting;
import earth.terrarium.heracles.api.client.settings.base.RegistryValueSetting;
import earth.terrarium.heracles.api.tasks.defaults.StructureTask;
import net.minecraft.Optionull;
import org.jetbrains.annotations.Nullable;

public class StructureTaskSettings implements SettingInitializer<StructureTask>, CustomizableQuestElementSettings<StructureTask> {

    public static final StructureTaskSettings INSTANCE = new StructureTaskSettings();

    @Override
    public CreationData create(@Nullable StructureTask object) {
        CreationData settings = CustomizableQuestElementSettings.super.create(object);
        settings.put("structure", RegistryValueSetting.STRUCTURE, Optionull.map(object, StructureTask::structures));
        settings.put("accurate", BooleanSetting.FALSE, Optionull.map(object, StructureTask::accurate));
        return settings;
    }

    @Override
    public StructureTask create(String id, @Nullable StructureTask object, Data data) {
        return create(object, data, (title, icon) -> new StructureTask(
            id,
            title,
            icon,
            data.get("structure", RegistryValueSetting.STRUCTURE).orElse(Optionull.mapOrDefault(object, StructureTask::structures, null)),
            data.get("accurate", BooleanSetting.FALSE).orElse(Optionull.mapOrDefault(object, StructureTask::accurate, false))
        ));
    }
}
