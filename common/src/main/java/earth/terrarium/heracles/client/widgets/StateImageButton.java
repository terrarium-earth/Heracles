package earth.terrarium.heracles.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import earth.terrarium.heracles.client.widgets.buttons.ThemedButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

import java.util.function.IntConsumer;

public class StateImageButton extends ImageButton implements ThemedButton {

    private final IntConsumer onPress;
    private final int states;

    private int selected;

    public StateImageButton(
        int x, int y,
        int width, int height,
        int xTexStart, int yTexStart, int diffTex,
        ResourceLocation resourceLocation, int textureWidth, int textureHeight,
        int states,
        IntConsumer onPress
    ) {
        super(
            x, y,
            width, height,
            xTexStart, yTexStart,
            diffTex,
            resourceLocation,
            textureWidth, textureHeight,
            b -> {}, CommonComponents.EMPTY
        );
        this.states = states;
        this.onPress = onPress;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int i, int j, float f) {
        int u = xTexStart;
        int v = yTexStart;
        if (this.isHovered()) {
            v += yDiffTex;
        }
        u += yDiffTex * selected;

        RenderSystem.enableDepthTest();
        graphics.blit(resourceLocation, getX(), getY(), u, v, width, height, textureWidth, textureHeight);
    }

    @Override
    public void onPress() {
        this.selected = (this.selected + 1) % this.states;
        this.onPress.accept(this.selected);
    }

    public int state() {
        return this.selected;
    }
}
