package earth.terrarium.heracles.api.client.settings.base;

import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.client.widgets.boxes.AutocompleteEditBox;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record RegistrySetting<T>(
    boolean allowsTags, Registry<T> registry
) implements Setting<T, AutocompleteEditBox<String>> {

    public static final RegistrySetting<EntityType<?>> ENTITY = new RegistrySetting<>(false, BuiltInRegistries.ENTITY_TYPE);
    public static final RegistrySetting<Item> ITEM = new RegistrySetting<>(false, BuiltInRegistries.ITEM);

    @Override
    public AutocompleteEditBox<String> createWidget(int width, T value) {
        AutocompleteEditBox<String> box = new AutocompleteEditBox<>(Minecraft.getInstance().font, 0, 0, width, 11,
            (text, item) -> item.contains(text) && !item.equals(text), Function.identity(), s -> {});
        List<String> suggestions = new ArrayList<>();
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
        return Optional.ofNullable(id)
            .flatMap(registry::getOptional)
            .orElse(null);
    }
}
