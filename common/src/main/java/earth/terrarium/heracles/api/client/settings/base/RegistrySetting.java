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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record RegistrySetting<T>(
    boolean allowsTags, ResourceKey<? extends Registry<T>> key
) implements Setting<T, AutocompleteEditBox<String>> {

    public static final RegistrySetting<EntityType<?>> ENTITY = new RegistrySetting<>(false, Registries.ENTITY_TYPE);
    public static final RegistrySetting<Item> ITEM = new RegistrySetting<>(false, Registries.ITEM);
    public static final RegistrySetting<ResourceLocation> STAT = new RegistrySetting<>(false, Registries.CUSTOM_STAT);

    @Override
    public AutocompleteEditBox<String> createWidget(int width, T value) {
        AutocompleteEditBox<String> box = new AutocompleteEditBox<>(Minecraft.getInstance().font, 0, 0, width, 11,
            (text, item) -> item.contains(text) && !item.equals(text), Function.identity(), s -> {});
        box.setMaxLength(Short.MAX_VALUE);
        List<String> suggestions = new ArrayList<>();
        var registry = Heracles.getRegistryAccess().registry(key).orElse(null);
        if (registry == null) {
            return box;
        }
        if (allowsTags) {
            registry.getTagNames().map(tag -> "#" + tag.location()).forEach(suggestions::add);
        }
        registry.keySet().stream().map(ResourceLocation::toString).forEach(suggestions::add);
        box.setSuggestions(suggestions);

        ResourceLocation id = Optionull.map(value, registry::getKey);
        box.setValue(id == null ? "" : id.toString());
        return box;
    }

    @Override
    public T getValue(AutocompleteEditBox<String> widget) {
        ResourceLocation id = ResourceLocation.tryParse(widget.getValue());
        var registry = Heracles.getRegistryAccess().registry(key).orElse(null);
        if (registry == null) {
            return null;
        }
        return Optional.ofNullable(id)
            .flatMap(registry::getOptional)
            .orElse(null);
    }
}
