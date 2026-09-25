package earth.terrarium.heracles.api.client.settings.base;

import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.client.components.widgets.textbox.DoubleTextBox;

import java.util.Objects;

public record DoubleSetting(double defaultValue) implements Setting<Double, DoubleTextBox> {

    public static final DoubleSetting INSTANCE = new DoubleSetting(0);

    @Override
    public DoubleTextBox createWidget(DoubleTextBox old, int width, Double value) {
        return new DoubleTextBox(old, width, 24, Objects.requireNonNullElse(value, defaultValue));
    }

    @Override
    public Double getValue(DoubleTextBox widget) {
        return widget.getDoubleValue().orElse(defaultValue);
    }
}
