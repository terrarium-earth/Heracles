package earth.terrarium.heracles.api.client.settings.tasks;

import com.mojang.datafixers.util.Either;
import earth.terrarium.heracles.api.client.settings.CustomizableQuestElementSettings;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.ItemSetting;
import earth.terrarium.heracles.api.tasks.defaults.ItemInteractTask;
import earth.terrarium.heracles.common.utils.RegistryValue;
import net.minecraft.Optionull;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class ItemInteractTaskSettings implements SettingInitializer<ItemInteractTask>, CustomizableQuestElementSettings<ItemInteractTask> {

    public static final ItemInteractTaskSettings INSTANCE = new ItemInteractTaskSettings();

    @Override
    public CreationData create(@Nullable ItemInteractTask object) {
        CreationData settings = CustomizableQuestElementSettings.super.create(object);
        settings.put("item", ItemSetting.INSTANCE, getDefaultItem(object));
        return settings;
    }

    @Override
    public ItemInteractTask create(String id, ItemInteractTask object, Data data) {
        var item = data.get("item", ItemSetting.INSTANCE).orElse(getDefaultItem(object));
        return create(object, data, (title, icon) -> new ItemInteractTask(id,
            title,
            icon,
            new RegistryValue<>(item.mapLeft(ItemStack::getItemHolder)),
            getComponents(item, getDefaultNbt(object))
        ));
    }

    private static Either<ItemStack, TagKey<Item>> getDefaultItem(ItemInteractTask object) {
        return Optionull.mapOrDefault(object,
            task -> task.item().getValue().map(item -> {
                ItemStack stack = new ItemStack(item);
                stack.applyComponents(task.components().asPatch());
                return Either.left(stack);
            }, Either::right),
            Either.left(Items.AIR.getDefaultInstance())
        );
    }

    private static DataComponentPredicate getDefaultNbt(ItemInteractTask object) {
        return Optionull.mapOrDefault(object, ItemInteractTask::components, DataComponentPredicate.EMPTY);
    }

    private static DataComponentPredicate getComponents(Either<ItemStack, TagKey<Item>> item, DataComponentPredicate backup) {
        return item.map(
            stack -> stack.getComponents().isEmpty() ? DataComponentPredicate.allOf(stack.getComponents()) : backup,
            tag -> backup
        );
    }
}
