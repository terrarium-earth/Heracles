package earth.terrarium.heracles.api.client.settings.base;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.client.widgets.boxes.AutocompleteEditBox;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
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
) implements Setting<ResourceKey<T>, AutocompleteEditBox<String>> {

    public static final RegistryKeySetting<Level> DIMENSION = new RegistryKeySetting<>(Registries.DIMENSION);

    @Override
    public AutocompleteEditBox<String> createWidget(int width, ResourceKey<T> value) {
        AutocompleteEditBox<String> box = new AutocompleteEditBox<>(Minecraft.getInstance().font, 0, 0, width, 11,
            (text, item) -> item.contains(text) && !item.equals(text), Function.identity(), s -> {});
        box.setMaxLength(Short.MAX_VALUE);
        List<String> suggestions = new ArrayList<>();
        var registry = Heracles.getRegistryAccess().registry(key).orElse(null);
        if (registry == null) {
            return box;
        }
        registry.keySet().stream().map(ResourceLocation::toString).forEach(suggestions::add);
        box.setSuggestions(suggestions);

        ResourceLocation id = Optionull.map(value, ResourceKey::location);
        box.setValue(id == null ? "" : id.toString());
        return box;
    }

    @Override
    public ResourceKey<T> getValue(AutocompleteEditBox<String> widget) {
        ResourceLocation id = ResourceLocation.tryParse(widget.getValue());
        return Optional.ofNullable(id)
            .map(ignored -> ResourceKey.create(key, id))
            .orElse(null);
    }
}
