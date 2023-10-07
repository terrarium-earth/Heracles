package earth.terrarium.heracles.api.client.settings.tasks;

import com.mojang.datafixers.util.Either;
import com.teamresourceful.resourcefullib.common.codecs.predicates.NbtPredicate;
import earth.terrarium.heracles.api.client.settings.CustomizableQuestElementSettings;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.RegistryValueSetting;
import earth.terrarium.heracles.api.tasks.defaults.ItemInteractTask;
import earth.terrarium.heracles.common.utils.RegistryValue;
import net.minecraft.Optionull;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class ItemInteractTaskSettings implements SettingInitializer<ItemInteractTask>, CustomizableQuestElementSettings<ItemInteractTask> {

    public static final ItemInteractTaskSettings INSTANCE = new ItemInteractTaskSettings();

    @Override
    public CreationData create(@Nullable ItemInteractTask object) {
        CreationData settings = CustomizableQuestElementSettings.super.create(object);
        settings.put("item", RegistryValueSetting.ITEM, getDefaultItem(object));
        return settings;
    }

    @Override
    public ItemInteractTask create(String id, ItemInteractTask object, Data data) {
        RegistryValue<Item> item = data.get("item", RegistryValueSetting.ITEM).orElse(getDefaultItem(object));
        NbtPredicate old = Optionull.mapOrDefault(object, ItemInteractTask::nbt, NbtPredicate.ANY);
        return create(object, data, (title, icon) -> new ItemInteractTask(id,
            title,
            icon,
            item,
            old
        ));
    }

    private static RegistryValue<Item> getDefaultItem(ItemInteractTask object) {
        return Optionull.mapOrDefault(object, ItemInteractTask::item, new RegistryValue<>(Either.left(Items.AIR.builtInRegistryHolder())));
    }
}
