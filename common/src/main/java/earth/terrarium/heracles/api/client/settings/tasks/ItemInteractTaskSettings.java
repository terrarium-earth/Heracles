package earth.terrarium.heracles.api.client.settings.tasks;

import com.mojang.datafixers.util.Either;
import com.teamresourceful.resourcefullib.common.codecs.predicates.NbtPredicate;
import earth.terrarium.heracles.api.client.settings.CustomizableQuestElementSettings;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.ItemSetting;
import earth.terrarium.heracles.api.tasks.defaults.ItemInteractTask;
import earth.terrarium.heracles.common.utils.RegistryValue;
import net.minecraft.Optionull;
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
            getNbt(item, getDefaultNbt(object))
        ));
    }

    private static Either<ItemStack, TagKey<Item>> getDefaultItem(ItemInteractTask object) {
        return Optionull.mapOrDefault(object,
            task -> task.item().getValue().map(item -> {
                ItemStack stack = new ItemStack(item);
                stack.setTag(task.nbt().tag());
                return Either.left(stack);
            }, Either::right),
            Either.left(Items.AIR.getDefaultInstance())
        );
    }

    private static NbtPredicate getDefaultNbt(ItemInteractTask object) {
        return Optionull.mapOrDefault(object, ItemInteractTask::nbt, NbtPredicate.ANY);
    }

    private static NbtPredicate getNbt(Either<ItemStack, TagKey<Item>> item, NbtPredicate backup) {
        return item.map(
            stack -> stack.hasTag() ? new NbtPredicate(stack.getTag()) : backup,
            tag -> backup
        );
    }
}
