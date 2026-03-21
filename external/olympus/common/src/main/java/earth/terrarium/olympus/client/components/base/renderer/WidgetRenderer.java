package earth.terrarium.olympus.client.components.base.renderer;

import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;

/**
 * A generic renderer to be used within a widget to render additional content.
 */
public interface WidgetRenderer<T extends AbstractWidget> {

    void render(GuiGraphics graphics, WidgetRendererContext<T> widget, float partialTick);

    default WidgetRenderer<T> withPadding(int padding) {
        return WidgetRenderers.padded(padding, padding, padding, padding, this);
    }

    default WidgetRenderer<T> withPadding(int vertical, int horizontal) {
        return WidgetRenderers.padded(vertical, horizontal, vertical, horizontal, this);
    }

    default WidgetRenderer<T> withPadding(int top, int right, int bottom, int left) {
        return WidgetRenderers.padded(top, right, bottom, left, this);
    }

    default WidgetRenderer<T> withCentered(int width, int height) {
        return WidgetRenderers.center(width, height, this);
    }

    default WidgetRenderer<T> withPaddingBottom(int padding) {
        return WidgetRenderers.padded(0, 0, padding, 0, this);
    }

    default WidgetRenderer<T> withPaddingTop(int padding) {
        return WidgetRenderers.padded(padding, 0, 0, 0, this);
    }

    default WidgetRenderer<T> withPaddingLeft(int padding) {
        return WidgetRenderers.padded(0, 0, 0, padding, this);
    }

    default WidgetRenderer<T> withPaddingRight(int padding) {
        return WidgetRenderers.padded(0, padding, 0, 0, this);
    }

    static <T extends AbstractWidget> WidgetRenderer<T> empty() {
        return (graphics, context, partialTick) -> {};
    }

}
