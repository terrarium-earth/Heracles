package earth.terrarium.heracles.api.client.settings.tasks;

import earth.terrarium.heracles.api.client.settings.CustomizableQuestElementSettings;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.TextSetting;
import earth.terrarium.heracles.api.tasks.defaults.DummyTask;
import net.minecraft.Optionull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DummyTaskSettings implements SettingInitializer<DummyTask>, CustomizableQuestElementSettings<DummyTask> {

    public static final DummyTaskSettings INSTANCE = new DummyTaskSettings();

    @Override
    public CreationData create(@Nullable DummyTask object) {
        CreationData settings = CustomizableQuestElementSettings.super.create(object);
        settings.put("id", TextSetting.INSTANCE, getDefaultId(object));
        settings.put("description", TextSetting.INSTANCE, getDefaultDescription(object));
        return settings;
    }

    @Override
    public DummyTask create(String id, DummyTask object, Data data) {
        return create(object, data, (title, icon) -> new DummyTask(
            id,
            title,
            icon,
            data.get("id", TextSetting.INSTANCE).orElse(getDefaultId(object)),
            data.get("description", TextSetting.INSTANCE).orElse(getDefaultDescription(object))
        ));
    }

    private static String getDefaultId(DummyTask object) {
        return Optionull.mapOrDefault(object, DummyTask::dummyId, UUID.randomUUID().toString());
    }

    private static String getDefaultDescription(DummyTask object) {
        return Optionull.mapOrDefault(object, DummyTask::description, "");
    }
}
