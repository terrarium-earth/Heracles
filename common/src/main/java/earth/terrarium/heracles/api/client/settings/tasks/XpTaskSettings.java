package earth.terrarium.heracles.api.client.settings.tasks;

import earth.terrarium.heracles.api.client.settings.CustomizableQuestElementSettings;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.EnumSetting;
import earth.terrarium.heracles.api.client.settings.base.IntSetting;
import earth.terrarium.heracles.api.tasks.CollectionType;
import earth.terrarium.heracles.api.tasks.defaults.XpTask;
import net.minecraft.Optionull;
import org.jetbrains.annotations.Nullable;

public class XpTaskSettings implements SettingInitializer<XpTask>, CustomizableQuestElementSettings<XpTask> {

    public static final XpTaskSettings INSTANCE = new XpTaskSettings();
    private static final EnumSetting<XpTask.XpType> TYPE = new EnumSetting<>(XpTask.XpType.class, XpTask.XpType.LEVEL);
    public static final EnumSetting<CollectionType> COLLECTION_TYPE = new EnumSetting<>(CollectionType.class, CollectionType.AUTOMATIC);

    @Override
    public CreationData create(@Nullable XpTask object) {
        CreationData settings = CustomizableQuestElementSettings.super.create(object);
        settings.put("type", TYPE, getDefaultType(object));
        settings.put("amount", IntSetting.ONE, getDefaultAmount(object));
        settings.put("collection_type", COLLECTION_TYPE, getDefaultCollectionType(object));
        return settings;
    }

    @Override
    public XpTask create(String id, @Nullable XpTask object, Data data) {
        return create(object, data, (title, icon) -> new XpTask(
            id,
            title,
            icon,
            data.get("amount", IntSetting.ONE).orElse(getDefaultAmount(object)),
            data.get("type", TYPE).orElse(getDefaultType(object)),
            data.get("collection_type", COLLECTION_TYPE).orElse(getDefaultCollectionType(object))
        ));
    }

    private static XpTask.XpType getDefaultType(XpTask object) {
        return Optionull.mapOrDefault(object, XpTask::xpType, XpTask.XpType.LEVEL);
    }

    private static int getDefaultAmount(XpTask object) {
        return Optionull.mapOrDefault(object, XpTask::target, 1);
    }

    private static CollectionType getDefaultCollectionType(XpTask object) {
        return Optionull.mapOrDefault(object, XpTask::collectionType, CollectionType.CONSUME);
    }
}

