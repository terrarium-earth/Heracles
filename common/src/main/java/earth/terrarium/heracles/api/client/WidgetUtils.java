package earth.terrarium.heracles.api.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskDisplayFormatter;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Matrix4f;

import java.util.List;
import java.util.function.Supplier;

public final class WidgetUtils {

    public static void drawBackground(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, 0x80808080);
        graphics.renderOutline(x, y, width, height, 0xFF909090);
    }

    public static <T extends Tag> void drawProgressBar(GuiGraphics graphics, int minX, int minY, int maxX, int maxY, QuestTask<?, T, ?> task, TaskProgress<T> progress) {
        graphics.fill(minX, minY, maxX, maxY, 0xFF808080);
        graphics.fill(minX + 1, minY + 1, maxX - 1, maxY - 1, 0xFF696969);
        float fill = task.getProgress(progress.progress());
        int progressWidth = (int) (((maxX - 1) - (minX + 1)) * fill);
        graphics.fill(minX + 1, minY + 1, minX + 1 + progressWidth, maxY - 1, progress.isComplete() ? 0xFF04CB40 : 0xFF5691FF);
    }

    public static <T extends Tag> void drawProgressText(GuiGraphics graphics, int x, int y, int width, QuestTask<?, T, ?> task, TaskProgress<T> progress) {
        Font font = Minecraft.getInstance().font;
        String text = QuestTaskDisplayFormatter.create(task, progress);
        graphics.drawString(
            font,
            text, x + width - 5 - font.width(text), y + 6, 0xFFFFFFFF,
            false
        );
    }

    public static void drawEntity(GuiGraphics graphics, int x, int y, int size, Entity entity) {
        y += size / 2f;
        x += size / 4f;
        Minecraft mc = Minecraft.getInstance();
        float scaledSize = 25 / (Math.max(entity.getBbWidth(), entity.getBbHeight()));
        if (Math.abs(entity.getBbHeight() - entity.getBbWidth()) > 5) {
            scaledSize *= 1.2f;
        } else if (Math.abs(entity.getBbHeight() - entity.getBbWidth()) == 0) {
            scaledSize *= 0.8f;
        }

        scaledSize *= size / 40f;

        x += size / 40f < 1 ? -6 * size / 40f : 5 * size / 40f;
        y += size / 40f < 1 ? -6 * size / 40f : 8 * size / 35f;

        float rot = 45f;
        if (entity instanceof EnderDragon) {
            // Ender dragon is rotated 180 degrees
            rot = 225f;
        }

        float entityY = y + 3 - (Math.max(0, size - entity.getBbHeight() * scaledSize));
        try (var pose = new CloseablePoseStack(graphics)) {
            pose.translate(14, 20 + 4, 0.5);
            pose.translate(x - 2, entityY, 1);
            pose.mulPose(Axis.ZP.rotationDegrees(180.0F));
            pose.translate(0, 0, 100);
            pose.scale(-(scaledSize), (scaledSize), 50);
            pose.mulPose(Axis.YP.rotationDegrees(rot));
            EntityRenderDispatcher entityRenderer = mc.getEntityRenderDispatcher();
            MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
            entityRenderer.render(entity, 0, 0, 0.0D, mc.getFrameTime(), 1, pose, buffer, LightTexture.FULL_BRIGHT);
            buffer.endBatch();
        }
    }

    public static boolean drawItemIcon(GuiGraphics graphics, ItemStack stack, int x, int y, int size) {
        if (stack != null && !stack.is(Items.AIR)) {
            int scale = size / 16;
            try (var pose = new CloseablePoseStack(graphics)) {
                pose.translate(x, y, 0);
                pose.scale(scale, scale, 1);
                graphics.renderFakeItem(stack, 0, 0);
            }
            return true;
        }
        return false;
    }

    public static void drawItemIconWithTooltip(GuiGraphics graphics, ItemStack icon, int x, int y, int size, Supplier<List<Component>> tooltipCallback, int mouseX, int mouseY) {
        WidgetUtils.drawItemIcon(graphics, icon, x, y, size);
        boolean inBounds = (mouseX >= x && mouseX < x + size) && (mouseY >= y && mouseY < y + size);
        if (inBounds) {
            List<Component> tooltipLines = tooltipCallback.get();
            if (tooltipLines != null) {
                ScreenUtils.setTooltip(tooltipLines);
            }
        }
    }

    public static void drawItemIconWithTooltip(GuiGraphics graphics, ItemStack icon, int x, int y, int iconSize, int mouseX, int mouseY) {
        drawItemIconWithTooltip(graphics, icon, x, y, iconSize, () -> Screen.getTooltipFromItem(Minecraft.getInstance(), icon), mouseX, mouseY);
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
                uOffset / (float) 256,
                (uOffset + uWidth) / (float) 256,
                (vOffset) / (float) 256,
                (vOffset + vHeight) / (float) 256
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
