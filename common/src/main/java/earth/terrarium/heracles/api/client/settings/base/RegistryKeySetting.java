package earth.terrarium.heracles.api.client.settings.base;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.client.components.widgets.textbox.autocomplete.AutocompleteTextBox;
import net.minecraft.Optionull;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record RegistryKeySetting<T>(
    ResourceKey<? extends Registry<T>> key
) implements Setting<ResourceKey<T>, AutocompleteTextBox<String>> {

    public static final RegistryKeySetting<Level> DIMENSION = new RegistryKeySetting<>(Registries.DIMENSION);

    @Override
    public AutocompleteTextBox<String> createWidget(AutocompleteTextBox<String> old, int width, ResourceKey<T> value) {
        ResourceLocation id = Optionull.map(value, ResourceKey::location);
        List<String> suggestions = new ArrayList<>();
        var registry = Heracles.getRegistryAccess().registry(key).orElse(null);
        if (registry != null) {
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
    public ResourceKey<T> getValue(AutocompleteTextBox<String> widget) {
        ResourceLocation id = ResourceLocation.tryParse(widget.value());
        return Optional.ofNullable(id)
            .map(ignored -> ResourceKey.create(key, id))
            .orElse(null);
    }
}
