package earth.terrarium.heracles.api.client.settings.base;

import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.client.components.widgets.textbox.IntTextBox;

import java.util.Objects;

public record IntSetting(int defaultValue) implements Setting<Integer, IntTextBox> {

    public static final IntSetting ZERO = new IntSetting(0);
    public static final IntSetting ONE = new IntSetting(1);

    @Override
    public IntTextBox createWidget(IntTextBox old, int width, Integer value) {
        return new IntTextBox(old, width, 24, Objects.requireNonNullElse(value, defaultValue), v -> {});
    }

    @Override
    public Integer getValue(IntTextBox widget) {
        return widget.getIntValue().orElse(defaultValue);
    }
}
