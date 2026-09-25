package earth.terrarium.heracles.client.components.widgets.textbox;

import earth.terrarium.olympus.client.components.textbox.TextBox;
import earth.terrarium.olympus.client.utils.ListenableState;
import net.minecraft.Optionull;

import java.text.DecimalFormat;
import java.util.OptionalDouble;

public class DoubleTextBox extends TextBox {

    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");

    protected static final int TEXT_COLOR = 0xe0e0e0;
    protected static final int ERROR_COLOR = 0xFF5555;

    public DoubleTextBox(DoubleTextBox old, int width, int height, double value) {
        super(ListenableState.of(Optionull.mapOrDefault(old, DoubleTextBox::getValue, FORMAT.format(value))));
        this.withSize(width, height);
        this.withMaxLength(Short.MAX_VALUE);
        this.withFilter(DoubleTextBox::filter);
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
