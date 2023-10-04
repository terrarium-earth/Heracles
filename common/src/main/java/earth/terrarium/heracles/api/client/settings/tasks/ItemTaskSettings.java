package earth.terrarium.heracles.api.client.settings.tasks;

import com.mojang.datafixers.util.Either;
import com.teamresourceful.resourcefullib.common.codecs.predicates.NbtPredicate;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.*;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.api.tasks.CollectionType;
import earth.terrarium.heracles.api.tasks.defaults.GatherItemTask;
import earth.terrarium.heracles.common.utils.RegistryValue;
import net.minecraft.Optionull;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class ItemTaskSettings implements SettingInitializer<GatherItemTask> {

    public static final ItemTaskSettings INSTANCE = new ItemTaskSettings();
    public static final EnumSetting<CollectionType> COLLECTION_TYPE = new EnumSetting<>(CollectionType.class, CollectionType.AUTOMATIC);

    @Override
    public CreationData create(@Nullable GatherItemTask object) {
        CreationData settings = new CreationData();
        settings.put("title", TextSetting.INSTANCE, Optionull.mapOrDefault(object, GatherItemTask::title, ""));
        settings.put("icon", QuestIconSetting.INSTANCE, getDefaultIcon(object));
        settings.put("item", RegistryValueSetting.ITEM, getDefaultItem(object));
        settings.put("amount", IntSetting.ONE, getDefaultCount(object));
        settings.put("collection_type", COLLECTION_TYPE, Optionull.mapOrDefault(object, GatherItemTask::collectionType, CollectionType.AUTOMATIC));
        return settings;
    }

    @Override
    public GatherItemTask create(String id, GatherItemTask object, Data data) {
        RegistryValue<Item> item = data.get("item", RegistryValueSetting.ITEM).orElse(getDefaultItem(object));
        ItemQuestIcon icon = data.get("icon", QuestIconSetting.INSTANCE).orElse(getDefaultIcon(object));
        NbtPredicate old = Optionull.mapOrDefault(object, GatherItemTask::nbt, NbtPredicate.ANY);
        return new GatherItemTask(
            id,
            data.get("title", TextSetting.INSTANCE).orElse(""),
            icon,
            item,
            old,
            data.get("amount", IntSetting.ONE).orElse(getDefaultCount(object)),
            data.get("collection_type", COLLECTION_TYPE).orElse(Optionull.mapOrDefault(object, GatherItemTask::collectionType, CollectionType.AUTOMATIC))
        );
    }

    private ItemQuestIcon getDefaultIcon(GatherItemTask object) {
        return Optionull.mapOrDefault(object, obj -> {
            if (obj.icon() instanceof ItemQuestIcon questIcon) return questIcon;
            return new ItemQuestIcon(Items.MAP);
        }, new ItemQuestIcon(Items.MAP));
    }

    private static RegistryValue<Item> getDefaultItem(GatherItemTask object) {
        return Optionull.mapOrDefault(object, GatherItemTask::item, new RegistryValue<>(Either.left(Items.AIR.builtInRegistryHolder())));
    }

    private static int getDefaultCount(GatherItemTask object) {
        return Optionull.mapOrDefault(object, GatherItemTask::target, 1);
    }
}
