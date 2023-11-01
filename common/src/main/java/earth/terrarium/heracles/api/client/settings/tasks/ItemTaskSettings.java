package earth.terrarium.heracles.api.client.settings.tasks;

import com.mojang.datafixers.util.Either;
import com.teamresourceful.resourcefullib.common.codecs.predicates.NbtPredicate;
import earth.terrarium.heracles.api.client.settings.CustomizableQuestElementSettings;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.EnumSetting;
import earth.terrarium.heracles.api.client.settings.base.IntSetting;
import earth.terrarium.heracles.api.client.settings.base.ItemSetting;
import earth.terrarium.heracles.api.tasks.CollectionType;
import earth.terrarium.heracles.api.tasks.defaults.GatherItemTask;
import earth.terrarium.heracles.common.utils.RegistryValue;
import net.minecraft.Optionull;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class ItemTaskSettings implements SettingInitializer<GatherItemTask>, CustomizableQuestElementSettings<GatherItemTask> {

    public static final ItemTaskSettings INSTANCE = new ItemTaskSettings();
    public static final EnumSetting<CollectionType> COLLECTION_TYPE = new EnumSetting<>(CollectionType.class, CollectionType.AUTOMATIC);

    @Override
    public CreationData create(@Nullable GatherItemTask object) {
        CreationData settings = CustomizableQuestElementSettings.super.create(object);
        settings.put("item", ItemSetting.INSTANCE, getDefaultItem(object));
        settings.put("amount", IntSetting.ONE, getDefaultCount(object));
        settings.put("collection_type", COLLECTION_TYPE, Optionull.mapOrDefault(object, GatherItemTask::collectionType, CollectionType.AUTOMATIC));
        return settings;
    }

    @Override
    public GatherItemTask create(String id, GatherItemTask object, Data data) {
        return create(object, data, (title, icon) -> {
            var item = data.get("item", ItemSetting.INSTANCE).orElse(getDefaultItem(object));
            return new GatherItemTask(
                id,
                title,
                icon,
                new RegistryValue<>(item.mapLeft(ItemStack::getItemHolder)),
                getNbt(item, getDefaultNbt(object)),
                data.get("amount", IntSetting.ONE).orElse(getDefaultCount(object)),
                data.get("collection_type", COLLECTION_TYPE).orElse(Optionull.mapOrDefault(object, GatherItemTask::collectionType, CollectionType.AUTOMATIC))
            );
        });
    }

    private static Either<ItemStack, TagKey<Item>> getDefaultItem(GatherItemTask object) {
        return Optionull.mapOrDefault(object,
            task -> task.item().getValue().map(item -> {
                ItemStack stack = new ItemStack(item);
                stack.setTag(task.nbt().tag());
                return Either.left(stack);
            }, Either::right),
            Either.left(Items.AIR.getDefaultInstance())
        );
    }

    private static int getDefaultCount(GatherItemTask object) {
        return Optionull.mapOrDefault(object, GatherItemTask::target, 1);
    }

    private static NbtPredicate getDefaultNbt(GatherItemTask object) {
        return Optionull.mapOrDefault(object, GatherItemTask::nbt, NbtPredicate.ANY);
    }

    private static NbtPredicate getNbt(Either<ItemStack, TagKey<Item>> item, NbtPredicate backup) {
        return item.map(
            stack -> stack.hasTag() ? new NbtPredicate(stack.getTag()) : backup,
            tag -> backup
        );
    }
}
