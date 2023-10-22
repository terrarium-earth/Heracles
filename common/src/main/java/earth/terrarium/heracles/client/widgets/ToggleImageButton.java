package earth.terrarium.heracles.client.widgets;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.resources.ResourceLocation;

public class ToggleImageButton extends StateImageButton {

    public ToggleImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation resourceLocation, int textureWidth, int textureHeight, BooleanConsumer onPress) {
        super(
            x, y,
            width, height,
            xTexStart, yTexStart, yDiffTex,
            resourceLocation, textureWidth, textureHeight,
            2, i -> onPress.accept(i == 1)
        );
    }
}
