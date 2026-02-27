package earth.terrarium.heracles.client.components.widgets.context;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.components.base.BaseWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class DividerWidget extends BaseWidget {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/context/divider.png");

    public DividerWidget() {
        super(0, 10);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blitRepeating(
            TEXTURE,
            this.getX(),
            this.getY(),
            this.getWidth(),
            this.getHeight(),
            0,
            0,
            256,
            10
        );
    }
}
