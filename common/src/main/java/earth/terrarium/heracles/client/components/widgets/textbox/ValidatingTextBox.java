package earth.terrarium.heracles.client.components.widgets.textbox;

import java.util.function.Predicate;

public class ValidatingTextBox extends TextBox {

    protected final Predicate<String> validator;

    public ValidatingTextBox(TextBox box, String value, int width, int height, Predicate<String> validator) {
        this(box, value, width, height, Short.MAX_VALUE, validator);
    }

    public ValidatingTextBox(TextBox box, String value, int width, int height, int maxLength, Predicate<String> validator) {
        super(box, value, width, height, maxLength);
        this.validator = validator;
    }

    @Override
    public int getTextColor() {
        return isValid() ? super.getTextColor() : ERROR_COLOR;
    }

    public boolean isValid() {
        return this.validator.test(this.value);
    }
}
