package earth.terrarium.heracles.api.client.settings.base;

import com.mojang.datafixers.util.Either;
import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.client.widgets.boxes.AutocompleteEditBox;
import earth.terrarium.heracles.common.utils.RegistryValue;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public record RegistryValueSetting<T>(
    Registry<T> registry) implements Setting<RegistryValue<T>, AutocompleteEditBox<String>> {

    public static final RegistryValueSetting<Item> ITEM = new RegistryValueSetting<>(BuiltInRegistries.ITEM);

    @Override
    public AutocompleteEditBox<String> createWidget(int width, RegistryValue<T> value) {
        AutocompleteEditBox<String> box = new AutocompleteEditBox<>(Minecraft.getInstance().font, 0, 0, width, 11,
            (text, item) -> item.contains(text) && !item.equals(text), Function.identity(), s -> {});
        List<String> suggestions = new ArrayList<>();
        registry.getTagNames().map(tag -> "#" + tag.location()).forEach(suggestions::add);
        registry.keySet().stream().map(ResourceLocation::toString).forEach(suggestions::add);
        box.setSuggestions(suggestions);

        box.setValue(Optionull.mapOrDefault(value, RegistryValue::toRegistryString, ""));
        return box;
    }

    @Override
    public RegistryValue<T> getValue(AutocompleteEditBox<String> widget) {
        if (widget.getValue().startsWith("#")) {
            ResourceLocation id = ResourceLocation.tryParse(widget.getValue().substring(1));
            return id == null ? null : new RegistryValue<>(Either.right(TagKey.create(registry.key(), id)));
        }
        return Optionull.map(
            ResourceLocation.tryParse(widget.getValue()), id -> registry.getHolder(ResourceKey.create(registry.key(), id))
                .map(RegistryValue::new)
                .orElse(null)
        );
    }
}