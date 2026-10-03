package earth.terrarium.heracles.client.components.widgets.context;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.utils.ClientUtils;
import earth.terrarium.olympus.client.components.base.BaseWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class DividerWidget extends BaseWidget {

    private static final ResourceLocation TEXTURE =  Heracles.id("textures/gui/sprites/context/divider.png");

    public DividerWidget() {
        super(0, 10);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        ClientUtils.blitTiling(
            graphics,
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
