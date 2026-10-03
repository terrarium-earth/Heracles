package earth.terrarium.heracles.client.components.widgets.textbox;

import earth.terrarium.olympus.client.components.textbox.TextBox;
import earth.terrarium.olympus.client.utils.ListenableState;
import net.minecraft.Optionull;

import java.util.OptionalInt;
import java.util.function.IntConsumer;

public class IntTextBox extends TextBox {

    protected static final int TEXT_COLOR = 0xe0e0e0;
    protected static final int ERROR_COLOR = 0xFF5555;

    public IntTextBox(IntTextBox old, int width, int height, int value, IntConsumer responder) {
        this(ListenableState.of(Optionull.mapOrDefault(old, IntTextBox::getValue, Integer.toString(value))), width, height, responder);
    }

    private IntTextBox(ListenableState<String> state, int width, int height, IntConsumer responder) {
        super(state);
        this.withSize(width, height);
        this.withMaxLength(Short.MAX_VALUE);
        this.withFilter(IntTextBox::filter);

        state.registerListener(s -> {
            s = s.trim();
            if (s.isEmpty() || s.equals("-")) {
                responder.accept(0);
            } else {
                try {
                    responder.accept(Integer.parseInt(s));
                } catch (NumberFormatException e) {
                    responder.accept(0);
                }
            }
        });
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
