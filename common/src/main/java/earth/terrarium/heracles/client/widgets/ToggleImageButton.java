package earth.terrarium.heracles.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

public class ToggleImageButton extends ImageButton {

    private final BooleanConsumer onPress;

    private boolean selected;

    public ToggleImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation resourceLocation, int textureWidth, int textureHeight, BooleanConsumer onPress) {
        super(
            x, y,
            width, height,
            xTexStart, yTexStart,
            yDiffTex,
            resourceLocation,
            textureWidth, textureHeight,
            b -> {}, CommonComponents.EMPTY
        );
        this.onPress = onPress;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int i, int j, float f) {
        int u = xTexStart;
        int v = yTexStart;
        if (this.isHovered()) {
            v += yDiffTex;
        }
        if (selected) {
            u += yDiffTex;
        }

        RenderSystem.enableDepthTest();
        graphics.blit(resourceLocation, getX(), getY(), u, v, width, height, textureWidth, textureHeight);
    }

    @Override
    public void onPress() {
        this.selected = !this.selected;
        this.onPress.accept(this.selected);
    }
}
