package earth.terrarium.olympus.client.components.renderers;

import com.teamresourceful.resourcefullib.common.color.Color;
import com.teamresourceful.resourcefullib.common.color.ConstantColors;
import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer;
import earth.terrarium.olympus.client.components.base.renderer.WidgetRendererContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;

public class SolidColorWidgetRenderer<T extends AbstractWidget> implements WidgetRenderer<T>, ColorableWidget {
    protected Color color = ConstantColors.white;
    protected boolean drawShadow = false;
    protected boolean ignoreAlpha = false;

    @Override
    public void render(GuiGraphics graphics, WidgetRendererContext<T> widget, float partialTick) {
        int color = this.ignoreAlpha ? this.color.getValue() | 0xFF000000 : this.color.getValue();
        graphics.fill(widget.getX(), widget.getY(), widget.getX() + widget.getWidth(), widget.getY() + widget.getHeight(), color);

        if (drawShadow) {
            var red = (int) ((this.color.getFloatRed() / 3f) * 255);
            var green = (int) ((this.color.getFloatGreen() / 3f) * 255);
            var blue = (int) ((this.color.getFloatBlue() / 3f) * 255);
            var shadowColor = new Color(red, green, blue, ignoreAlpha ? 0xFF : this.color.getIntAlpha());
            graphics.fill(widget.getX() + 1, widget.getY() + 1, widget.getX() + widget.getWidth() + 1, widget.getY() + widget.getHeight() + 1, shadowColor.getValue());
        }
    }

    @Override
    public SolidColorWidgetRenderer<T> withColor(Color color) {
        this.color = color;
        return this;
    }

    @Override
    public SolidColorWidgetRenderer<T> withShadow() {
        this.drawShadow = true;
        return this;
    }

    public SolidColorWidgetRenderer<T> withoutAlpha() {
        this.ignoreAlpha = true;
        return this;
    }
}
