package earth.terrarium.olympus.client.components.map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

public class MapRenderer {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("olympus","dynamic_map");
    private final int scale;

    public MapRenderer(int[][] colors, int scale) {
        var textureManager = Minecraft.getInstance().getTextureManager();
        var dynamicTexture = new DynamicTexture("Olympus Map Texture", scale, scale, true);
        textureManager.register(TEXTURE, dynamicTexture);
        updateTexture(dynamicTexture, colors, scale);
        this.scale = scale;
    }

    private void updateTexture(DynamicTexture texture, int[][] colors, int scale) {
        var nativeImage = texture.getPixels();
        if (nativeImage == null) return;

        for (int i = 0; i < scale; i++) {
            for (int j = 0; j < scale; j++) {
                nativeImage.setPixel(i, j, colors[i][j]);
            }
        }

        texture.upload();
    }

    public int getScale() {
        return scale;
    }

    public void render(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.blit(TEXTURE, x, y, x + width, y + height, 0f, 1f, 0f, 1f);
    }
}