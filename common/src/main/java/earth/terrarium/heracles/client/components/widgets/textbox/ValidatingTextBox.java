package earth.terrarium.heracles.client.components.widgets.textbox;

import earth.terrarium.olympus.client.components.textbox.TextBox;
import earth.terrarium.olympus.client.utils.ListenableState;
import net.minecraft.Optionull;

import java.util.function.Predicate;

public class ValidatingTextBox extends TextBox {

    protected static final int ERROR_COLOR = 0xFF5555;

    protected final Predicate<String> validator;

    public ValidatingTextBox(ValidatingTextBox box, String value, int width, int height, Predicate<String> validator) {
        this(box, value, width, height, Short.MAX_VALUE, validator);
    }

    public ValidatingTextBox(ValidatingTextBox box, String value, int width, int height, int maxLength, Predicate<String> validator) {
        super(ListenableState.of(Optionull.mapOrDefault(box, ValidatingTextBox::getValue, value)));
        this.validator = validator;
        this.withSize(width, height);
        this.withMaxLength(maxLength);
    }

    @Override
    public int getTextColor() {
        return isValid() ? super.getTextColor() : ERROR_COLOR;
    }

    public boolean isValid() {
        return this.validator.test(this.getValue());
    }
}
