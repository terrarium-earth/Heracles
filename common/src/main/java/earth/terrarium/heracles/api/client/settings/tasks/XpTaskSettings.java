package earth.terrarium.heracles.api.client.settings.tasks;

import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.EnumSetting;
import earth.terrarium.heracles.api.client.settings.base.IntSetting;
import earth.terrarium.heracles.api.tasks.defaults.XpTask;
import net.minecraft.Optionull;
import org.jetbrains.annotations.Nullable;

public class XpTaskSettings implements SettingInitializer<XpTask> {

    private static final EnumSetting<XpTask.XpType> TYPE = new EnumSetting<>(XpTask.XpType.class, XpTask.XpType.LEVEL);

    public static final XpTaskSettings INSTANCE = new XpTaskSettings();

    @Override
    public CreationData create(@Nullable XpTask object) {
        CreationData settings = new CreationData();
        settings.put("type", TYPE, getDefaultType(object));
        settings.put("amount", IntSetting.ONE, getDefaultAmount(object));
        return settings;
    }

    @Override
    public XpTask create(String id, @Nullable XpTask object, Data data) {
        return new XpTask(
            id,
            data.get("amount", IntSetting.ONE).orElse(getDefaultAmount(object)),
            data.get("type", TYPE).orElse(getDefaultType(object))
        );
    }

    private static XpTask.XpType getDefaultType(XpTask object) {
        return Optionull.mapOrDefault(object, XpTask::xpType, XpTask.XpType.LEVEL);
    }

    private static int getDefaultAmount(XpTask object) {
        return Optionull.mapOrDefault(object, XpTask::target, 1);
    }
}

