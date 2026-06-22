package earth.terrarium.heracles.client.components.widgets.textbox;

import net.minecraft.Optionull;

import java.util.OptionalInt;
import java.util.function.IntConsumer;

public class IntTextBox extends TextBox {

    public IntTextBox(TextBox old, int width, int height, int value, IntConsumer responder) {
        super(
            old, Optionull.mapOrDefault(value, i -> Integer.toString(i), "0"),
            width, height,
            Short.MAX_VALUE, IntTextBox::filter, s -> {
                s = s.trim();
                if (s.isEmpty() || s.equals("-")) {
                    responder.accept(0);
                } else {
                    try {
                        responder.accept(Integer.parseInt(s));
                    }catch (NumberFormatException e) {
                        responder.accept(0);
                    }
                }
            }
        );
    }

    private static boolean filter(String value) {
        if (value.isEmpty() || value.equals("-")) {
            return true;
        }
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void setIfNotFocused(int value) {
        if (!isFocused()) {
            getIntValue().ifPresentOrElse(current -> {
                if (current != value) {
                    setValue(Integer.toString(value));
                }
            }, () -> {
                setValue(Integer.toString(value));
            });
        }
    }

    public OptionalInt getIntValue() {
        if (getValue().isEmpty()) {
            return OptionalInt.empty();
        }
        try {
            return OptionalInt.of(Integer.parseInt(getValue()));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }

    @Override
    public int getTextColor() {
        try {
            Integer.parseInt(getValue());
            return TEXT_COLOR;
        } catch (NumberFormatException e) {
            return ERROR_COLOR;
        }
    }
}