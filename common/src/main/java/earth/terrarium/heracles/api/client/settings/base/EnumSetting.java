package earth.terrarium.heracles.api.client.settings.base;

import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.client.components.widgets.dropdown.Dropdown;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.LinkedHashMap;
import java.util.Map;

public record EnumSetting<T extends Enum<T> & StringRepresentable>(
    Class<T> enumClass, T defaultValue
) implements Setting<T, Dropdown<T>> {

    @Override
    public Dropdown<T> createWidget(Dropdown<T> old, int width, T value) {
        Map<T, Component> options = new LinkedHashMap<>();
        for (T enumValue : enumClass.getEnumConstants()) {
            options.put(enumValue, toComponent(enumValue));
        }
        return new Dropdown<>(old, width, 24, options, value);
    }

    private Component toComponent(T value) {
        return Component.translatable(value.getSerializedName(), value.name().charAt(0) + value.name().substring(1).toLowerCase());
    }

    @Override
    public T getValue(Dropdown<T> widget) {
        return widget.selected();
    }
}
