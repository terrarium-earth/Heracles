package earth.terrarium.olympus.client.components.dropdown;

import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer;
import earth.terrarium.olympus.client.components.buttons.Button;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.ui.OverlayAlignment;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.olympus.client.ui.context.ContextMenu;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.function.Consumers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class DropdownBuilder<T> {
    private final DropdownState<T> state;
    private final List<T> options = new ArrayList<>();

    private OverlayAlignment alignment = OverlayAlignment.BOTTOM_LEFT;
    private Identifier background = UIConstants.LIST_BG;
    private WidgetSprites entrySprites = UIConstants.LIST_ENTRY;
    private Function<T, WidgetRenderer<? super Button>> entryRenderer = t -> WidgetRenderers.text(CommonComponents.ELLIPSIS);
    private Consumer<T> action = Consumers.nop();

    private int width = -1;
    private int height = 150;
    private int entryHeight = 20;

    public DropdownBuilder(DropdownState<T> state) {
        this.state = state;
    }

    public DropdownBuilder<T> withOptions(List<T> options) {
        this.options.addAll(options);
        return this;
    }

    public DropdownBuilder<T> withOption(T value) {
        this.options.add(value);
        return this;
    }

    public DropdownBuilder<T> withTexture(Identifier sprite) {
        this.background = sprite;
        return this;
    }

    public DropdownBuilder<T> withAlignment(OverlayAlignment alignment) {
        this.alignment = alignment;
        return this;
    }

    public DropdownBuilder<T> withEntrySprites(WidgetSprites sprites) {
        this.entrySprites = sprites;
        return this;
    }

    public DropdownBuilder<T> withEntryRenderer(Function<T, WidgetRenderer<? super Button>> renderer) {
        this.entryRenderer = renderer;
        return this;
    }

    public DropdownBuilder<T> withSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public DropdownBuilder<T> withEntryHeight(int height) {
        this.entryHeight = height;
        return this;
    }

    public DropdownBuilder<T> withCallback(Consumer<T> action) {
        this.action = action;
        return this;
    }

    public Button build() {
        return state.getButton().withCallback(() -> {
            int entryWidth = width == -1 ? state.getButton().getWidth() : width;
            state.setOpened(true);
            ContextMenu.open(ctx -> {
                ctx.withBounds(entryWidth, height)
                        .withAlignment(alignment, state)
                        .withTexture(background)
                        .withCloseCallback(() -> state.setOpened(false));

                for (T option : options) {
                    ctx.add(() -> Widgets.button()
                            .withTexture(entrySprites)
                            .withRenderer(entryRenderer.apply(option))
                            .withSize(entryWidth, entryHeight)
                            .withCallback(() -> {
                                state.set(option);
                                action.accept(option);
                            })
                    );
                }
            });
        });
    }
}
