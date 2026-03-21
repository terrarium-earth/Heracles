package earth.terrarium.olympus.client.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Objects;

public interface Translatable {

    String getTranslationKey();

    static Component toComponent(Object value) {
        return toComponent(value, Component.empty());
    }

    static Component toComponent(Object value, Component defaultValue) {
        if (value instanceof Translatable translatable) {
            return Component.translatable(translatable.getTranslationKey());
        }
        if (value instanceof StringRepresentable string) {
            return Component.literal(string.getSerializedName());
        }
        if (value == null) {
            return defaultValue;
        }
        return Component.literal(Objects.toString(value));
    }
}