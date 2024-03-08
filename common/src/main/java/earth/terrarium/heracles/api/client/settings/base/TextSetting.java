package earth.terrarium.heracles.api.client.settings.base;

import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.client.components.widgets.textbox.ValidatingTextBox;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.function.Function;
import java.util.function.Predicate;

public record TextSetting<O>(
    Predicate<String> filter, Function<String, O> encoder, Function<O, String> decoder
) implements Setting<O, ValidatingTextBox> {

    public static final TextSetting<String> INSTANCE = new TextSetting<>(s -> true, Function.identity(), Function.identity());
    public static final TextSetting<ResourceLocation> RESOURCELOCATION = new TextSetting<>(
        ResourceLocation::isValidResourceLocation,
        ResourceLocation::tryParse,
        id -> id == null ? "" : id.toString()
    );
    public static final TextSetting<ResourceKey<Level>> DIMENSION = new TextSetting<>(
        ResourceLocation::isValidResourceLocation,
        s -> ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(s)),
        key -> key == null ? "" : key.location().toString()
    );

    @Override
    public ValidatingTextBox createWidget(ValidatingTextBox old, int width, O value) {
        return new ValidatingTextBox(old, decoder.apply(value), width, 24, filter);
    }

    @Override
    public O getValue(ValidatingTextBox widget) {
        return widget.isValid() ? encoder.apply(widget.getValue()) : null;
    }
}
