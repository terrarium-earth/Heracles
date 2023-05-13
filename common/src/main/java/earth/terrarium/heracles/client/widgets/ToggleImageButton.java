package earth.terrarium.heracles.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
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
    public void renderWidget(PoseStack poseStack, int i, int j, float f) {
        RenderSystem.setShaderTexture(0, resourceLocation);
        int u = xTexStart;
        int v = yTexStart;
        if (this.isHovered()) {
            v += yDiffTex;
        }
        if (selected) {
            u += yDiffTex;
        }

        RenderSystem.enableDepthTest();
        blit(poseStack, getX(), getY(), u, v, width, height, textureWidth, textureHeight);
    }

    @Override
    public void onPress() {
        this.selected = !this.selected;
        this.onPress.accept(this.selected);
    }
}
