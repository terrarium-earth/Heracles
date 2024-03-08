package earth.terrarium.heracles.api.client.settings.base;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.client.components.widgets.textbox.autocomplete.AutocompleteTextBox;
import net.minecraft.Optionull;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record RegistrySetting<T>(
    boolean allowsTags, ResourceKey<? extends Registry<T>> key
) implements Setting<T, AutocompleteTextBox<String>> {

    public static final RegistrySetting<EntityType<?>> ENTITY = new RegistrySetting<>(false, Registries.ENTITY_TYPE);
    public static final RegistrySetting<Item> ITEM = new RegistrySetting<>(false, Registries.ITEM);
    public static final RegistrySetting<ResourceLocation> STAT = new RegistrySetting<>(false, Registries.CUSTOM_STAT);

    @Override
    public AutocompleteTextBox<String> createWidget(AutocompleteTextBox<String> old, int width, T value) {
        var registry = Heracles.getRegistryAccess().registry(key).orElse(null);
        ResourceLocation id = Optionull.map(registry, reg -> Optionull.map(value, reg::getKey));
        List<String> suggestions = new ArrayList<>();
        if (registry != null) {
            if (allowsTags) {
                registry.getTagNames().map(tag -> "#" + tag.location()).forEach(suggestions::add);
            }
            registry.keySet().stream().map(ResourceLocation::toString).forEach(suggestions::add);
        }
        return new AutocompleteTextBox<>(
            old, Optionull.mapOrDefault(id, ResourceLocation::toString, ""),
            width, 24,
            suggestions,
            (text, item) -> item.contains(text) && !item.equals(text), Function.identity()
        );
    }

    @Override
    public T getValue(AutocompleteTextBox<String> widget) {
        ResourceLocation id = ResourceLocation.tryParse(widget.value());
        var registry = Heracles.getRegistryAccess().registry(key).orElse(null);
        if (registry == null) {
            return null;
        }
        return Optional.ofNullable(id)
            .flatMap(registry::getOptional)
            .orElse(null);
    }
}
