package earth.terrarium.olympus.client.components.compound.radio;

import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer;
import earth.terrarium.olympus.client.components.base.renderer.WidgetRendererContext;
import earth.terrarium.olympus.client.components.compound.LayoutWidget;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.layouts.Layouts;
import earth.terrarium.olympus.client.layouts.LinearViewLayout;
import earth.terrarium.olympus.client.ui.UIConstants;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.CommonComponents;
import org.apache.commons.lang3.function.Consumers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class RadioBuilder<T> {
    private final List<T> options = new ArrayList<>();
    private final RadioState<T> state;
    private BiFunction<T, Boolean, WidgetRenderer<AbstractWidget>> entryRenderer = (ignored, depressed) -> WidgetRenderers.text(CommonComponents.ELLIPSIS);
    private Function<T, WidgetSprites> entrySprites = (ignored) -> UIConstants.BUTTON;
    private Consumer<T> action = Consumers.nop();

    private int width = 120;
    private int height = 20;
    private int gap = 0;

    private boolean allowDeselection = false;

    public RadioBuilder(RadioState<T> state) {
        this.state = state;
    }

    public RadioBuilder<T> withOptions(List<T> options) {
        this.options.addAll(options);
        return this;
    }

    public RadioBuilder<T> withOption(T value) {
        this.options.add(value);
        return this;
    }

    public RadioBuilder<T> withRenderer(BiFunction<T, Boolean, WidgetRenderer<AbstractWidget>> renderer) {
        this.entryRenderer = renderer;
        return this;
    }

    public RadioBuilder<T> withoutEntrySprites() {
        this.entrySprites = (ignored) -> null;
        return this;
    }

    public RadioBuilder<T> withEntrySprites(WidgetSprites sprites) {
        this.entrySprites = (ignored) -> sprites;
        return this;
    }

    public RadioBuilder<T> withEntrySprites(Function<T, WidgetSprites> sprites) {
        this.entrySprites = sprites;
        return this;
    }

    public RadioBuilder<T> withGap(int gap) {
        this.gap = gap;
        return this;
    }

    public RadioBuilder<T> withDeselection() {
        this.allowDeselection = true;
        return this;
    }

    public RadioBuilder<T> withCallback(Consumer<T> action) {
        this.action = action;
        return this;
    }

    public RadioBuilder<T> withSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public LayoutWidget<LinearViewLayout> build() {
        return new LayoutWidget<>(Layouts.row().withGap(gap)).withContents(layout -> {
            for (int index = 0; index < options.size(); index++) {
                T option = options.get(index);
                int finalIndex = index;
                layout.withChild(Widgets.button()
                        .withSize((width - gap * (options.size() - 1)) / options.size(), height)
                        .withRenderer((graphics, widget, partialTick) -> {
                            WidgetRenderer<AbstractWidget> apply = entryRenderer.apply(option, state.getIndex() == finalIndex);
                            WidgetRendererContext<AbstractWidget> context = new WidgetRendererContext<AbstractWidget>(widget.getWidget(), widget.getMouseX(), widget.getMouseY()).setWidth(widget.getWidth()).setHeight(widget.getHeight());
                            apply.render(graphics, context, partialTick);
                        })
                        .withTexture(entrySprites.apply(option))
                        .withCallback(() -> {
                            if (state.getIndex() != finalIndex) {
                                state.setIndex(finalIndex);
                                state.set(option);
                                action.accept(option);
                            } else if (allowDeselection) {
                                state.setIndex(-1);
                                state.set(null);
                                action.accept(null);
                            }
                        }));
            }
        }).withStretchToContentSize();
    }
}
