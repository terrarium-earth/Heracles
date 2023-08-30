package earth.terrarium.heracles.api.client.settings.tasks;

import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.RegistrySetting;
import earth.terrarium.heracles.api.client.settings.base.TextSetting;
import earth.terrarium.heracles.api.tasks.defaults.DummyTask;
import net.minecraft.Optionull;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Function;

public class DummyTaskSettings implements SettingInitializer<DummyTask> {

    public static final DummyTaskSettings INSTANCE = new DummyTaskSettings();

    @Override
    public CreationData create(@Nullable DummyTask object) {
        CreationData settings = new CreationData();
        settings.put("id", TextSetting.INSTANCE, getDefaultId(object));
        settings.put("icon", RegistrySetting.ITEM, getDefaultIcon(object));
        settings.put("title", TextSetting.INSTANCE, getDefaultText(object, DummyTask::title));
        settings.put("description", TextSetting.INSTANCE, getDefaultText(object, DummyTask::description));
        return settings;
    }

    @Override
    public DummyTask create(String id, DummyTask object, Data data) {
        return new DummyTask(
            id,
            data.get("id", TextSetting.INSTANCE).orElse(getDefaultId(object)),
            data.get("icon", RegistrySetting.ITEM).orElse(getDefaultIcon(object)),
            toComponent(data.get("title", TextSetting.INSTANCE).orElse(getDefaultText(object, DummyTask::title))),
            toComponent(data.get("description", TextSetting.INSTANCE).orElse(getDefaultText(object, DummyTask::description)))
        );
    }

    private static String getDefaultId(DummyTask object) {
        return Optionull.mapOrDefault(object, DummyTask::dummyId, UUID.randomUUID().toString());
    }

    private static Item getDefaultIcon(DummyTask object) {
        return Optionull.mapOrDefault(object, DummyTask::icon, Items.BARRIER);
    }

    private static String getDefaultText(DummyTask object, Function<DummyTask, Component> getter) {
        return Optionull.mapOrDefault(object, task -> {
            try {
                return Component.Serializer.toJson(getter.apply(task));
            } catch (Exception e) {
                return getter.apply(task).getString();
            }
        }, "");
    }

    private static Component toComponent(String text) {
        try {
            Component component = Component.Serializer.fromJson(text);
            return component != null ? component : Component.nullToEmpty(text);
        } catch (Exception e) {
            return Component.nullToEmpty(text);
        }
    }
}
