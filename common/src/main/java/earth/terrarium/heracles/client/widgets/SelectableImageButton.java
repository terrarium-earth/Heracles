package earth.terrarium.heracles.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

public class SelectableImageButton extends ImageButton {

    private boolean selected;

    public SelectableImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation resourceLocation, int textureWidth, int textureHeight, Button.OnPress onPress) {
        super(
            x, y,
            width, height,
            xTexStart, yTexStart,
            yDiffTex,
            resourceLocation,
            textureWidth, textureHeight,
            onPress, CommonComponents.EMPTY
        );
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int i, int j, float f) {
        int r = yTexStart;
        if (selected || this.isHovered()) {
            r += yDiffTex;
        }

        RenderSystem.enableDepthTest();
        graphics.blit(resourceLocation, getX(), getY(), this.xTexStart, r, width, height, textureWidth, textureHeight);
    }

    @Override
    public void onPress() {
        if (!this.selected) {
            super.onPress();
        }
        this.selected = true;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return this.selected;
    }
}
