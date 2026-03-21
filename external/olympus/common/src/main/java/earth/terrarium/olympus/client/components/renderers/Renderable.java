package earth.terrarium.olympus.client.components.renderers;

import earth.terrarium.olympus.client.components.base.BaseWidget;
import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer;
import earth.terrarium.olympus.client.components.base.renderer.WidgetRendererContext;
import net.minecraft.client.gui.GuiGraphics;

public class Renderable extends BaseWidget  {

    private final WidgetRenderer<? super Renderable> renderer;

    public Renderable(WidgetRenderer<? super Renderable> renderer) {
        this.renderer = renderer;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderer.render(graphics, new WidgetRendererContext<>(this, mouseX, mouseY), partialTick);
    }
}
