package earth.terrarium.heracles.api.client.settings.base;

import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.buttons.Button;
import earth.terrarium.olympus.client.components.dropdown.DropdownState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;

public record EnumSetting<T extends Enum<T> & StringRepresentable>(
    Class<T> enumClass, T defaultValue
) implements Setting<T, Button> {

    private static final WeakHashMap<Button, DropdownState<?>> DATA = new WeakHashMap<>();

    @Override
    public Button createWidget(Button old, int width, T value) {
        @SuppressWarnings("unchecked")
        DropdownState<T> state = old != null && DATA.containsKey(old)
            ? (DropdownState<T>) DATA.get(old)
            : DropdownState.of(value);
        List<T> options = Arrays.asList(enumClass.getEnumConstants());
        Button button = Widgets.dropdown(
            state,
            options,
            this::toComponent,
            btn -> btn.withSize(width, 24),
            dropdown -> {}
        );
        DATA.put(button, state);
        return button;
    }

    private Component toComponent(T value) {
        return Component.translatable(value.getSerializedName(), value.name().charAt(0) + value.name().substring(1).toLowerCase());
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getValue(Button widget) {
        DropdownState<T> state = (DropdownState<T>) DATA.get(widget);
        return state != null ? state.get() : defaultValue;
    }
}
