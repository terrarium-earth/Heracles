package earth.terrarium.heracles.client.components.quest.editor.overlays.color;

import com.mojang.blaze3d.systems.RenderSystem;
import earth.terrarium.heracles.Heracles;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class ColorButton extends Button {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/editor/color/color.png");

    private final Supplier<ChatFormatting> color;

    protected ColorButton(Supplier<ChatFormatting> color, Component component, Runnable onPress) {
        super(0, 0, 16, 16, component, b -> onPress.run(), DEFAULT_NARRATION);

        this.color = color;
        this.setTooltip(Tooltip.create(getMessage()));
    }

    public static ColorButton of(Supplier<ChatFormatting> color, Component component, Runnable onPress) {
        return new ColorButton(color, component, onPress);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Integer color = this.color.get().getColor();
        if (color == null) return;
        graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), color | 0xFF000000);
        RenderSystem.enableBlend();
        graphics.blit(TEXTURE, this.getX(), this.getY(), 0, 0, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight());
        RenderSystem.disableBlend();

//        RenderSystem.setShaderTexture(0, TEXTURE);
//        RenderSystem.setShader(GameRenderer::getposition);
//        Matrix4f matrix4f = this.pose.last().pose();
//        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
//        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
//        bufferBuilder.vertex(matrix4f, (float)x1, (float)y1, (float)blitOffset).uv(minU, minV).endVertex();
//        bufferBuilder.vertex(matrix4f, (float)x1, (float)y2, (float)blitOffset).uv(minU, maxV).endVertex();
//        bufferBuilder.vertex(matrix4f, (float)x2, (float)y2, (float)blitOffset).uv(maxU, maxV).endVertex();
//        bufferBuilder.vertex(matrix4f, (float)x2, (float)y1, (float)blitOffset).uv(maxU, minV).endVertex();
//        BufferUploader.drawWithShader(bufferBuilder.end());
    }
}
