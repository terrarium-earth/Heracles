package earth.terrarium.heracles.common.utils;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.function.BiFunction;
import java.util.function.Function;

public record RegistryValue<T>(Either<Holder<T>, TagKey<T>> value) {

    public RegistryValue(Holder<T> value) {
        this(Either.left(value));
    }

    public RegistryValue(TagKey<T> value) {
        this(Either.right(value));
    }

    public static <T> Codec<RegistryValue<T>> codec(ResourceKey<? extends Registry<T>> registry) {
        return Codec.either(RegistryFixedCodec.create(registry), TagKey.hashedCodec(registry))
            .xmap(RegistryValue::new, RegistryValue::value);
    }

    public Either<T, TagKey<T>> getValue() {
        return this.value.mapLeft(Holder::value);
    }

    public boolean isTag() {
        return this.value.right().isPresent();
    }

    public Component getDisplayName(Function<T, Component> mapper) {
        return getValue().map(mapper, RegistryValue::getDisplayName);
    }

    public Component getDisplayName(BiFunction<ResourceLocation, T, Component> mapper) {
        return this.value.map(
            holder -> mapper.apply(holder.unwrapKey().map(ResourceKey::location).orElse(null), holder.value()),
            RegistryValue::getDisplayName
        );
    }

    private static Component getDisplayName(TagKey<?> tag, boolean pathOnlyFallback) {
        String namespace = tag.registry().location().getNamespace();
        String path = tag.registry().location().getPath();

        String translationKey;
        if (namespace.equals("minecraft")) {
            translationKey = "tag." + path + "." + tag.location().getNamespace() + "." + tag.location().getPath();
        } else {
            translationKey = "tag." + namespace + "." + path + "." + tag.location().getNamespace() + "." + tag.location().getPath();
        }
        return Component.translatableWithFallback(translationKey, "#" + (pathOnlyFallback ? tag.location().getPath() : tag.location()));
    }

    public static Component getShortDisplayName(TagKey<?> tag) {
        return getDisplayName(tag, true);
    }

    public static Component getDisplayName(TagKey<?> tag) {
        return getDisplayName(tag, false);
    }

    public String toRegistryString() {
        return value
            .map(
                holder -> holder.unwrapKey()
                    .map(ResourceKey::location)
                    .map(ResourceLocation::toString)
                    .orElse(null),
                tag -> "#" + tag.location()
            );
    }

    public boolean is(Holder<T> value) {
        return this.value.map((v) -> v.equals(value), value::is);
    }
}
