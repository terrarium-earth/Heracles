package earth.terrarium.heracles.api.client.settings.base;

import com.mojang.datafixers.util.Either;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.client.components.widgets.textbox.autocomplete.AutocompleteTextBox;
import earth.terrarium.heracles.common.utils.BiOptional;
import earth.terrarium.heracles.common.utils.RegistryValue;
import net.minecraft.Optionull;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record RegistryValueSetting<T>(
    ResourceKey<? extends Registry<T>> key
) implements Setting<RegistryValue<T>, AutocompleteTextBox<String>> {

    public static final RegistryValueSetting<Item> ITEM = new RegistryValueSetting<>(Registries.ITEM);
    public static final RegistryValueSetting<Block> BLOCK = new RegistryValueSetting<>(Registries.BLOCK);
    public static final RegistryValueSetting<Structure> STRUCTURE = new RegistryValueSetting<>(Registries.STRUCTURE);
    public static final RegistryValueSetting<Biome> BIOME = new RegistryValueSetting<>(Registries.BIOME);
    public static final RegistryValueSetting<EntityType<?>> ENTITY = new RegistryValueSetting<>(Registries.ENTITY_TYPE);

    @Override
    public AutocompleteTextBox<String> createWidget(AutocompleteTextBox<String> old, int width, RegistryValue<T> value) {
        Registry<T> registry = Heracles.getRegistryAccess().registry(key).orElse(null);
        List<String> suggestions = new ArrayList<>();
        if (registry != null) {
            registry.getTagNames().map(tag -> "#" + tag.location()).forEach(suggestions::add);
            registry.keySet().stream().map(ResourceLocation::toString).forEach(suggestions::add);
        }
        return new AutocompleteTextBox<>(
            old, Optionull.mapOrDefault(value, RegistryValue::toRegistryString, ""),
            width, 24,
            suggestions,
            (text, item) -> item.contains(text) && !item.equals(text), Function.identity()
        );
    }

    @Override
    public RegistryValue<T> getValue(AutocompleteTextBox<String> widget) {
        String value = widget.value();
        if (value.startsWith("#")) {
            ResourceLocation id = ResourceLocation.tryParse(value.substring(1));
            return id == null ? null : new RegistryValue<>(Either.right(TagKey.create(key, id)));
        }

        return BiOptional.flatMap(
            Heracles.getRegistryAccess().registry(key),
            Optional.ofNullable(ResourceLocation.tryParse(value)),
            (registry, id) -> registry.getHolder(ResourceKey.create(key, id))
        )
        .map(RegistryValue::new)
        .orElse(null);
    }
}
