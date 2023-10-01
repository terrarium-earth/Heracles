package earth.terrarium.heracles.api.client;

import com.mojang.math.Axis;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskDisplayFormatter;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public final class WidgetUtils {

    public static void drawBackground(GuiGraphics graphics, int x, int y, int width) {
        graphics.fill(x, y, x + width, y + (int) (width * 0.1f) + 10, 0x80808080);
        graphics.renderOutline(x, y, width, (int) (width * 0.1f) + 10, 0xFF909090);
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
            text, x + width - 5 - font.width(text), y + 5, 0xFFFFFFFF,
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

    public static void drawItemIcon(GuiGraphics graphics, ItemStack icon, int x, int y, int iconSize) {
        graphics.renderFakeItem(icon, x + 5 + (int) (iconSize / 2f) - 8, y + 5 + (int) (iconSize / 2f) - 8);
    }

    public static void drawItemIconWithTooltip(GuiGraphics graphics, ItemStack icon, int x, int y, int iconSize, Font font, Supplier<List<Component>> tooltipCallback, int mouseX, int mouseY) {
        WidgetUtils.drawItemIcon(graphics, icon, x, y, iconSize);
        boolean inBounds = (mouseX >= x + 8 && mouseX < x + 24) && (mouseY >= y + 8 && mouseY < y + 24);
        if (inBounds) {
            List<Component> tooltipLines = tooltipCallback.get();
            if (tooltipLines != null) {
                graphics.renderTooltip(font, tooltipLines, Optional.empty(), mouseX, mouseY);
            }
        }
    }

    public static void drawItemIconWithTooltip(GuiGraphics graphics, ItemStack icon, int x, int y, int iconSize, Font font, int mouseX, int mouseY) {
        drawItemIconWithTooltip(graphics, icon, x, y, iconSize, font, () -> Screen.getTooltipFromItem(Minecraft.getInstance(), icon), mouseX, mouseY);
    }
}
