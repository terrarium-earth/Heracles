package earth.terrarium.heracles.client.components.widgets.textbox;

import net.minecraft.Optionull;

import java.text.DecimalFormat;
import java.util.OptionalDouble;

public class DoubleTextBox extends TextBox {

    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");

    public DoubleTextBox(TextBox old, int width, int height, double value) {
        super(
            old, Optionull.mapOrDefault(value, FORMAT::format, "0"),
            width, height,
            Short.MAX_VALUE, DoubleTextBox::filter, s -> {}
        );
    }

    private static boolean filter(String value) {
        if (value.isEmpty() || value.equals("-")) {
            return true;
        }
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void setIfNotFocused(double value) {
        if (!isFocused()) {
            getDoubleValue().ifPresentOrElse(current -> {
                if (current != value) {
                    setValue(Double.toString(value));
                }
            }, () -> {
                setValue(Double.toString(value));
            });
        }
    }

    public OptionalDouble getDoubleValue() {
        if (getValue().isEmpty()) {
            return OptionalDouble.empty();
        }
        try {
            return OptionalDouble.of(Double.parseDouble(getValue()));
        } catch (NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }

    @Override
    public int getTextColor() {
        try {
            Double.parseDouble(getValue());
            return TEXT_COLOR;
        } catch (NumberFormatException e) {
            return ERROR_COLOR;
        }
    }
}