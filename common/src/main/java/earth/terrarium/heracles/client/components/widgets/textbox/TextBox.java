package earth.terrarium.heracles.client.components.widgets.textbox;

import earth.terrarium.olympus.client.utils.ListenableState;
import net.minecraft.Optionull;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class TextBox extends earth.terrarium.olympus.client.components.textbox.TextBox {

    // Legacy constants referenced by subclasses (e.g., IntTextBox/DoubleTextBox)
    protected static final int TEXT_COLOR = 0xe0e0e0;
    protected static final int ERROR_COLOR = 0xFF5555;
    protected static final int PLACEHOLDER_COLOR = 0xD0D0D0;
    protected static final int PADDING = 4;

    private final ListenableState<String> state;

    public TextBox(TextBox box, String value, int width, int height, int maxLength) {
        this(box, value, width, height, maxLength, s -> true, s -> {
        });
    }

    public TextBox(TextBox box, String value, int width, int height, int maxLength, Predicate<String> filter, Consumer<String> responder) {
        this(ListenableState.of(Optionull.mapOrDefault(box, TextBox::getValue, value)), width, height, maxLength, filter, responder);
    }

    private TextBox(ListenableState<String> state, int width, int height, int maxLength, Predicate<String> filter, Consumer<String> responder) {
        super(state);
        this.state = state;
        this.withSize(width, height);
        this.withMaxLength(maxLength);
        this.withFilter(filter);

        this.state.registerListener(responder);
    }

    public ListenableState<String> state() {
        return this.state;
    }
}
