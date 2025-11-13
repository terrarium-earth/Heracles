package earth.terrarium.heracles.api.client.settings.base;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.client.widgets.boxes.OptionalAutocompleteEditBox;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record OptionalRegistryKeySetting<T>(
    ResourceKey<? extends Registry<T>> key
) implements Setting<@Nullable ResourceKey<T>, OptionalAutocompleteEditBox<String>> {

    public static final OptionalRegistryKeySetting<Level> DIMENSION = new OptionalRegistryKeySetting<>(Registries.DIMENSION);

    @Override
    public OptionalAutocompleteEditBox<String> createWidget(int width, ResourceKey<T> value) {
        OptionalAutocompleteEditBox<String> box = new OptionalAutocompleteEditBox<>(Minecraft.getInstance().font, 0, 0, width, 11,
            (text, item) -> item.contains(text) && !item.equals(text), Function.identity(), s -> {});
        box.setMaxLength(Short.MAX_VALUE);
        List<String> suggestions = new ArrayList<>();
        var registry = Heracles.getRegistryAccess().registry(key).orElse(null);
        if (registry == null) {
            return box;
        }
        registry.keySet().stream().map(ResourceLocation::toString).forEach(suggestions::add);
        box.setSuggestions(suggestions);

        String id = Optionull.map(value, key -> key.location().toString());
        box.setValue(id);
        return box;
    }

    @Override
    public ResourceKey<T> getValue(OptionalAutocompleteEditBox<String> widget) {
        String value = widget.nullableValue();
        if (value == null) return null;
        ResourceLocation id = ResourceLocation.tryParse(value);
        return Optional.ofNullable(id)
            .map(ignored -> ResourceKey.create(key, id))
            .orElse(null);
    }
}
