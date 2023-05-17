package earth.terrarium.heracles.common.utils;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.function.BiPredicate;
import java.util.function.Function;

public record RegistryValue<T, K>(
    Either<T, TagKey<K>> value
) {

    public static <T, K> Codec<RegistryValue<T, K>> codec(Codec<T> baseCodec, ResourceKey<? extends Registry<K>> key) {
        return Codec.either(baseCodec, TagKey.hashedCodec(key))
            .xmap(RegistryValue::new, RegistryValue::value);
    }

    public static <T> Codec<RegistryValue<T, T>> codec(Registry<T> registry) {
        return codec(registry.byNameCodec(), registry.key());
    }

    public Either<T, TagKey<K>> getValue() {
        return value;
    }

    public Component getDisplayName(Function<T, Component> mapper) {
        return this.value.map(mapper, RegistryValue::getDisplayName);
    }

    private static Component getDisplayName(TagKey<?> tag) {
        String namespace = tag.registry().location().getNamespace();
        String path = tag.registry().location().getPath();

        String translationKey;
        String fallbackKey;
        if (namespace.equals("minecraft")) {
            translationKey = "tag." + path + "." + tag.location().getNamespace() + "." + tag.location().getPath();
            fallbackKey = path + "#" + tag.location();
        } else {
            translationKey = "tag." + namespace + "." + path + "." + tag.location().getNamespace() + "." + tag.location().getPath();
            fallbackKey = namespace + "/" + path + "#" + tag.location();
        }
        return Component.translatableWithFallback(translationKey, fallbackKey);
    }

    public boolean is(T value, Function<T, Holder.Reference<K>> mapper) {
        return this.value.map(
            (v) -> v.equals(value),
            (tag) -> mapper.apply(value).is(tag)
        );
    }

    public boolean is(T value, BiPredicate<T, TagKey<K>> predicate) {
        return this.value.map(
            (v) -> v.equals(value),
            (tag) -> predicate.test(value, tag)
        );
    }

    public boolean is(Holder<K> value) {
        return this.value.map((v) -> value.isBound() && v.equals(value.value()), value::is);
    }
}
