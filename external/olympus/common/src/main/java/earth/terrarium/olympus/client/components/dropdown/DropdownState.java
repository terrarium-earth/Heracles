package earth.terrarium.olympus.client.components.dropdown;

import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer;
import earth.terrarium.olympus.client.components.buttons.Button;
import earth.terrarium.olympus.client.utils.State;
import net.minecraft.client.gui.components.AbstractWidget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class DropdownState<T> implements State<T> {

    private final State<T> state;
    private Button button;
    private boolean openState;

    public DropdownState(Button button, State<T> state, boolean openState) {
        this.button = button;
        this.state = state;
        this.openState = openState;
    }

    public static <T> DropdownState<T> of(T state) {
        return new DropdownState<>(null, State.of(state), false);
    }

    public static <T> DropdownState<T> empty() {
        return DropdownState.of(null);
    }

    public <W extends AbstractWidget> WidgetRenderer<W> withRenderer(BiFunction<@Nullable T, Boolean, @NotNull WidgetRenderer<W>> text) {
        return (graphics, context, partialTick) -> text.apply(this.get(), this.openState).render(graphics, context, partialTick);
    }

    public Button getButton() {
        return button;
    }

    public boolean isOpened() {
        return openState;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public void setOpened(boolean openState) {
        this.openState = openState;
    }

    @Override
    public void set(T value) {
        this.state.set(value);
    }

    @Override
    public T get() {
        return this.state.get();
    }
}
