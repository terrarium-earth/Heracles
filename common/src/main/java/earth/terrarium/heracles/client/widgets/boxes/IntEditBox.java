package earth.terrarium.heracles.client.widgets.boxes;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.OptionalInt;
import java.util.function.IntConsumer;

public class IntEditBox extends EditBox {

    public IntEditBox(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
        setFilter(s -> {
            if (s.isEmpty() || s.equals("-")) {
                return true;
            }
            try {
                Integer.parseInt(s);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    public void setNumberResponder(IntConsumer responder) {
        setResponder(s -> {
            if (s.isEmpty() || s.equals("-")) {
                responder.accept(0);
            } else {
                responder.accept(Integer.parseInt(s));
            }
        });
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
}
