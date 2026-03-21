package earth.terrarium.heracles.api.client.settings.base;

import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.client.components.widgets.ToggleSwitch;

public record BooleanSetting(
    boolean defaultValue
) implements Setting<Boolean, ToggleSwitch> {

    public static final BooleanSetting FALSE = new BooleanSetting(false);
    public static final BooleanSetting TRUE = new BooleanSetting(true);

    @Override
    public ToggleSwitch createWidget(ToggleSwitch old, int width, Boolean value) {
        return new ToggleSwitch(value);
    }

    @Override
    public Boolean getValue(ToggleSwitch widget) {
        return widget.isToggled();
    }
}
