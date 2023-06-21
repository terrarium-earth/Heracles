package earth.terrarium.heracles.api.client.settings.base;

import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.client.widgets.boxes.ValidatingEditBox;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.function.Function;
import java.util.function.Predicate;

public record TextSetting<O>(Predicate<String> charFilter, Predicate<String> filter, Function<String, O> encoder,
                             Function<O, String> decoder) implements Setting<O, ValidatingEditBox> {

    public static final TextSetting<String> INSTANCE = new TextSetting<>(s -> true, s -> true, Function.identity(), Function.identity());
    public static final TextSetting<ResourceLocation> RESOURCELOCATION = new TextSetting<>(
        s -> s.chars().mapToObj(i -> (char) i).allMatch(ResourceLocation::isAllowedInResourceLocation),
        ResourceLocation::isValidResourceLocation,
        ResourceLocation::tryParse,
        id -> id == null ? "" : id.toString()
    );
    public static final TextSetting<ResourceKey<Level>> DIMENSION = new TextSetting<>(
        s -> s.chars().mapToObj(i -> (char) i).allMatch(ResourceLocation::isAllowedInResourceLocation),
        ResourceLocation::isValidResourceLocation,
        s -> ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(s)),
        key -> key == null ? "" : key.location().toString()
    );

    @Override
    public ValidatingEditBox createWidget(int width, O value) {
        ValidatingEditBox box = new ValidatingEditBox(Minecraft.getInstance().font, 0, 0, width, 11, CommonComponents.EMPTY, filter);
        box.setFilter(charFilter);
        box.setValue(decoder.apply(value));
        box.setMaxLength(32767);
        return box;
    }

    @Override
    public O getValue(ValidatingEditBox widget) {
        return widget.isValid() ? encoder.apply(widget.getValue()) : null;
    }
}
