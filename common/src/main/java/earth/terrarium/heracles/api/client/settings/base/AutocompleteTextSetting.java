package earth.terrarium.heracles.api.client.settings.base;

import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.client.handlers.ClientAdvancementDisplays;
import earth.terrarium.heracles.client.widgets.boxes.AutocompleteEditBox;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Locale;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

public record AutocompleteTextSetting<T>(
    Supplier<List<T>> suggestions, BiPredicate<String, T> filter, Function<T, String> mapper
) implements Setting<T, AutocompleteEditBox<T>> {

    public static final AutocompleteTextSetting<ResourceLocation> ALL_RECIPES = new AutocompleteTextSetting<>(
        () -> {
            var connection = Minecraft.getInstance().getConnection();
            if (connection == null) {
                return List.of();
            }
            return connection.getRecipeManager().getRecipeIds().toList();
        },
        (text, item) -> item.toString().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) && !item.toString().equalsIgnoreCase(text),
        r -> Optionull.mapOrDefault(r, ResourceLocation::toString, "")
    );

    public static final AutocompleteTextSetting<ResourceLocation> ALL_ADVANCEMENT = new AutocompleteTextSetting<>(
        () -> ClientAdvancementDisplays.getAdvancements().stream().toList(),
        (text, item) -> item.toString().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) && !item.toString().equalsIgnoreCase(text),
        r -> Optionull.mapOrDefault(r, ResourceLocation::toString, "")
    );

    @Override
    public AutocompleteEditBox<T> createWidget(int width, T value) {
        AutocompleteEditBox<T> box = new AutocompleteEditBox<>(Minecraft.getInstance().font, 0, 0, width, 11, filter, mapper, s -> {});
        box.setSuggestions(this.suggestions.get());
        box.setValue(mapper.apply(value));
        box.setMaxLength(Short.MAX_VALUE);
        return box;
    }

    @Override
    public T getValue(AutocompleteEditBox<T> widget) {
        return widget.value();
    }
}
