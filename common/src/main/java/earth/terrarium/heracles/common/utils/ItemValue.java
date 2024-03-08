package earth.terrarium.heracles.common.utils;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefullib.common.codecs.recipes.ItemStackCodec;
import earth.terrarium.heracles.Heracles;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class ItemValue extends RegistryValue<Item> {
    public static Codec<ItemValue> CODEC = Codec.either(
        ItemStackCodec.CODEC,
        TagKey.hashedCodec(Registries.ITEM)
    ).xmap(ItemValue::new, ItemValue::item);

    private final Either<ItemStack, TagKey<Item>> item;
    private @Nullable List<ItemStack> values;

    public ItemValue(Either<ItemStack, TagKey<Item>> item, @Nullable List<ItemStack> values) {
        super(item.mapLeft(i -> i.getItem().builtInRegistryHolder()));
        this.item = item;
        this.values = values;
    }

    public ItemValue(Either<ItemStack, TagKey<Item>> item) {
        this(item, item.map(s -> List.of(s.copy()), t -> null));
    }

    public ItemValue(TagKey<Item> key) {
        this(Either.right(key));
    }

    public ItemValue(ItemLike item) {
        this(Either.left(item.asItem().getDefaultInstance()));
    }

    public ItemValue(ItemStack stack) {
        this(Either.left(stack.copy()));
    }

    public Component getDisplayName() {
        return this.item.map(
            ItemStack::getHoverName,
            RegistryValue::getShortDisplayName
        );
    }

    public ResourceLocation getId() {
        return this.item.map(
            i -> BuiltInRegistries.ITEM.getKey(i.getItem()),
            TagKey::location
        );
    }

    public Component getNamespace() {
        return Component.literal(CompatUtils.guessModTitle(getId().getNamespace())).withStyle(Style.EMPTY.withColor(ChatFormatting.BLUE).withItalic(true));
    }

    public ItemStack getDefaultInstance() {
        if (this.values().isEmpty()) return Items.AIR.getDefaultInstance();
        int index = (int) (System.currentTimeMillis() / 2000) % this.values().size();
        return this.values().get(index);
    }

    public Either<ItemStack, TagKey<Item>> item() {
        return item;
    }

    public boolean isEmpty() {
        return item.map(
            stack -> stack.is(Items.AIR),
            key -> Heracles.getRegistryAccess()
                .registry(Registries.ITEM)
                .map(registry -> registry.getTag(key).isEmpty())
                .orElse(true)
        );
    }

    public List<ItemStack> values() {
        if (values == null) {
            values = item.map(
                stack -> List.of(stack.copy()),
                key -> Heracles.getRegistryAccess().registry(Registries.ITEM).map(registry -> registry.getTag(key)
                    .map(tag -> tag.stream().map(Holder::value).map(ItemStack::new).toList())
                    .orElse(List.of())
                ).orElse(List.of()));
        }
        return values;
    }

    public ItemValue copy() {
        return new ItemValue(this.item.mapLeft(ItemStack::copy));
    }
}
