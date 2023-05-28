package earth.terrarium.heracles.client.widgets.modals.icon.background;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import earth.terrarium.heracles.client.utils.ClientUtils;
import earth.terrarium.heracles.client.widgets.modals.upload.UploadModal;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public record BackgroundModalItem(ResourceLocation texture) {

    public static final int WIDTH = 152;
    public static final int HEIGHT = 28;

    public void render(GuiGraphics graphics, ScissorBoxStack ignored, int x, int y, int mouseX, int mouseY, boolean hovering) {
        graphics.blit(UploadModal.TEXTURE, x, y, 0, 173, WIDTH, HEIGHT, 256, 256);

        RenderSystem.setShaderTexture(0, texture);

        Matrix4f matrix = graphics.pose().last().pose();

        int xStart = (WIDTH - 72) / 2;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix, x + xStart, y + 2, 0).uv(0, 0).endVertex();
        bufferBuilder.vertex(matrix, x + xStart, y + 26, 0).uv(0, 1).endVertex();
        bufferBuilder.vertex(matrix, x + xStart + 72, y + 26, 0).uv(1, 1).endVertex();
        bufferBuilder.vertex(matrix, x + xStart + 72, y + 2, 0).uv(1, 0).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());

        if (hovering && mouseX >= x && mouseX <= x + WIDTH && mouseY >= y && mouseY <= y + HEIGHT) {
            CursorUtils.setCursor(true, CursorScreen.Cursor.POINTER);
            String textureName = texture.getNamespace() + ":" + texture.getPath().substring("textures/gui/quest_backgrounds/".length());
            ClientUtils.setTooltip(Component.literal(textureName));
        }
    }
}
