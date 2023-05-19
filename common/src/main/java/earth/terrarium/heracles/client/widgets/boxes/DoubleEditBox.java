package earth.terrarium.heracles.client.widgets.boxes;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.OptionalDouble;
import java.util.function.DoubleConsumer;

public class DoubleEditBox extends EditBox {

    public DoubleEditBox(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
        setFilter(s -> {
            if (s.isEmpty() || s.equals("-")) {
                return true;
            }
            try {
                Double.parseDouble(s);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    public void setNumberResponder(DoubleConsumer responder) {
        setResponder(s -> {
            if (s.isEmpty() || s.equals("-")) {
                responder.accept(0);
            } else {
                responder.accept(Double.parseDouble(s));
            }
        });
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
}
