package earth.terrarium.heracles.client.utils;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.util.Collection;

public class ClientUtils {

    public static boolean hasWorldFileAccess() {
        return Minecraft.getInstance().getSingleplayerServer() != null;
    }

    public static Collection<ResourceLocation> getTextures(String path) {
        var textures = Minecraft.getInstance().getResourceManager()
            .listResources("textures/" + path, location -> location.getPath().endsWith(".png"));
        return textures.keySet();
    }

    public static Screen screen() {
        return Minecraft.getInstance().screen;
    }

    public static MouseClick getMousePos() {
        MouseHandler mouse = Minecraft.getInstance().mouseHandler;
        Window window = Minecraft.getInstance().getWindow();
        double mouseX = mouse.xpos() * (double) window.getGuiScaledWidth() / (double) window.getScreenWidth();
        double mouseY = mouse.ypos() * (double) window.getGuiScaledHeight() / (double) window.getScreenHeight();
        return new MouseClick((int) mouseX, (int) mouseY, -1);
    }

    public static void blitTiling(GuiGraphics graphics, ResourceLocation atlasLocation, int targetX, int targetY, int targetWidth, int targetHeight, int sourceX, int sourceY, int sourceWidth, int sourceHeight) {
        try (BlitBatcher batcher = new BlitBatcher(graphics, atlasLocation)) {
            int xOffset = 0;
            while (xOffset < targetWidth) {
                int width = Math.min(targetWidth - xOffset, sourceWidth);
                int yOffset = 0;
                while (yOffset < targetHeight) {
                    int height = Math.min(targetHeight - yOffset, sourceHeight);
                    batcher.addBlit(targetX + xOffset, targetY + yOffset, sourceX, sourceY, width, height);
                    yOffset += height;
                }
                xOffset += width;
            }
        }
    }

    public static final class BlitBatcher implements AutoCloseable {
        private final BufferBuilder bufferBuilder;
        private final Matrix4f matrix4f;

        public BlitBatcher(GuiGraphics graphics, ResourceLocation atlasLocation) {
            RenderSystem.setShaderTexture(0, atlasLocation);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            this.matrix4f = graphics.pose().last().pose();
            this.bufferBuilder = Tesselator.getInstance().getBuilder();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        }

        public void addBlit(int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {
            this.innerAddBlit(x, x + uWidth, y, y + vHeight,
                uOffset / 256f,
                (uOffset + uWidth) / 256f,
                (vOffset) / 256f,
                (vOffset + vHeight) / 256f
            );
        }

        private void innerAddBlit(int x1, int x2, int y1, int y2, float minU, float maxU, float minV, float maxV) {
            bufferBuilder.vertex(matrix4f, x1, y1, 0).uv(minU, minV).endVertex();
            bufferBuilder.vertex(matrix4f, x1, y2, 0).uv(minU, maxV).endVertex();
            bufferBuilder.vertex(matrix4f, x2, y2, 0).uv(maxU, maxV).endVertex();
            bufferBuilder.vertex(matrix4f, x2, y1, 0).uv(maxU, minV).endVertex();
        }

        @Override
        public void close() {
            BufferUploader.drawWithShader(bufferBuilder.end());
        }
    }
}
