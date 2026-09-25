package earth.terrarium.heracles.api.client.settings.base;

import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.buttons.Button;
import earth.terrarium.olympus.client.utils.ListenableState;

import java.util.Objects;
import java.util.WeakHashMap;

public record BooleanSetting(
    boolean defaultValue
) implements Setting<Boolean, Button> {

    public static final BooleanSetting FALSE = new BooleanSetting(false);
    public static final BooleanSetting TRUE = new BooleanSetting(true);

    private static final WeakHashMap<Button, ListenableState<Boolean>> DATA = new WeakHashMap<>();

    @Override
    public Button createWidget(Button old, int width, Boolean value) {
        ListenableState<Boolean> state = old != null && DATA.containsKey(old)
            ? DATA.get(old)
            : ListenableState.of(Objects.requireNonNullElse(value, defaultValue));
        state.set(Objects.requireNonNullElse(value, defaultValue));
        Button button = Widgets.toggle(state).withSize(30, 16);
        DATA.put(button, state);
        return button;
    }

    @Override
    public Boolean getValue(Button widget) {
        ListenableState<Boolean> state = DATA.get(widget);
        return state != null ? state.get() : defaultValue;
    }
}