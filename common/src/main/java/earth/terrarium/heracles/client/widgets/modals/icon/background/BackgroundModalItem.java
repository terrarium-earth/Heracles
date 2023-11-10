package earth.terrarium.heracles.client.widgets.modals.icon.background;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import earth.terrarium.heracles.client.utils.TexturePlacements;
import earth.terrarium.heracles.client.widgets.modals.upload.UploadModal;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public record BackgroundModalItem(ResourceLocation texture) {

    public static final int WIDTH = 152;

    public int height() {
        return TexturePlacements.getOrDefault(texture, TexturePlacements.NO_OFFSET_24X).height() + 4;
    }

    public void render(GuiGraphics graphics, ScissorBoxStack ignored, int x, int y, int mouseX, int mouseY, boolean hovering) {
        TexturePlacements.Info info = TexturePlacements.getOrDefault(texture, TexturePlacements.NO_OFFSET_24X);

        graphics.blitNineSliced(UploadModal.TEXTURE, x, y, WIDTH, info.height() + 4, 3, 152, 28, 0, 173);

        RenderSystem.setShaderTexture(0, texture);
        Matrix4f matrix = graphics.pose().last().pose();

        int xStart = (WIDTH - info.width() * 5) / 2;

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix, x + xStart, y + 2, 0).uv(0, 0).endVertex();
        bufferBuilder.vertex(matrix, x + xStart, y + 2 + info.height(), 0).uv(0, 1).endVertex();
        bufferBuilder.vertex(matrix, x + xStart + info.width() * 5, y + 2 + info.height(), 0).uv(1, 1).endVertex();
        bufferBuilder.vertex(matrix, x + xStart + info.width() * 5, y + 2, 0).uv(1, 0).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());
        RenderSystem.disableBlend();

        if (hovering && mouseX >= x && mouseX <= x + WIDTH && mouseY >= y && mouseY <= y + info.height() + 4) {
            CursorUtils.setCursor(true, CursorScreen.Cursor.POINTER);
            String textureName = texture.getNamespace() + ":" + texture.getPath().substring("textures/gui/quest_backgrounds/".length());
            ScreenUtils.setTooltip(Component.literal(textureName));
        }
    }
}
