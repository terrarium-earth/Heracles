package earth.terrarium.olympus.client.components.compound.radio;

import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer;
import earth.terrarium.olympus.client.utils.State;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.function.BiFunction;

public class RadioState<T> implements State<T> {
    private T value;
    private int index;

    public RadioState(T value, int index) {
        this.value = value;
        this.index = index;
    }

    public static <T> RadioState<T> of(T value, int index) {
        return new RadioState<>(value, index);
    }

    public static <T> RadioState<T> empty() {
        return new RadioState<>(null, -1);
    }

    public static <T extends Enum<?>> RadioState<T> ofEnum(T value) {
        return new RadioState<>(value, value.ordinal());
    }

    @Override
    public void set(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return this.value;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public <W extends AbstractWidget> WidgetRenderer<W> withRenderer(int index, BiFunction<T, Boolean, WidgetRenderer<W>> text) {
        return (graphics, context, partialTick) -> text.apply(this.get(), this.index == index).render(graphics, context, partialTick);
    }
}
