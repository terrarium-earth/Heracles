package earth.terrarium.heracles.api.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.client.theme.QuestScreenTheme;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskDisplayFormatter;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
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

import java.util.List;
import java.util.function.Supplier;

public final class WidgetUtils {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/widgets.png");
    public static final ResourceLocation BACKGROUND = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/screen/widgets.png");

    public static void drawBackground(GuiGraphics graphics, int x, int y, int width, int height) {
        RenderSystem.enableBlend();
        graphics.blitNineSliced(BACKGROUND, x, y, 42, height, 3, 42, 42, 0, 0);
        graphics.blitNineSliced(BACKGROUND, x + 42, y, width - 42, height, 3, 86, 42, 42, 0);
        RenderSystem.disableBlend();
    }

    public static void drawSummaryBackground(GuiGraphics graphics, int x, int y, int width, int height) {
        drawStatusSummaryBackground(graphics, x, y, width, height, ModUtils.QuestStatus.IN_PROGRESS);
    }

    public static void drawStatusSummaryBackground(GuiGraphics graphics, int x, int y, int width, int height, ModUtils.QuestStatus status) {
        RenderSystem.enableBlend();
        graphics.blitNineSliced(TEXTURE, x, y, width, height, 3, 128, 42, 128, 42 * status.ordinal());
        RenderSystem.disableBlend();
    }

    public static <T extends Tag> void drawProgressBar(GuiGraphics graphics, int minX, int minY, int maxX, int maxY, QuestTask<?, T, ?> task, TaskProgress<T> progress) {
        RenderSystem.enableBlend();
        graphics.blitNineSliced(TEXTURE, minX, minY, maxX - minX, maxY - minY, 3, 128, 8, 0, 168 + (progress.isComplete() ? 8 : 0));
        float fill = Math.min(1f, task.getProgress(progress.progress()));
        if (fill != 0.0 && !progress.isComplete()) {
            int progressWidth = (int) ((maxX - minX) * fill);
            graphics.blitNineSliced(TEXTURE, minX, minY, progressWidth, maxY - minY, 3, 128, 8, 0, 168 + 8 + 8);
        }
        RenderSystem.disableBlend();
    }

    public static <T extends Tag> void drawProgressText(GuiGraphics graphics, int x, int y, int width, QuestTask<?, T, ?> task, TaskProgress<T> progress) {
        Font font = Minecraft.getInstance().font;
        String text = QuestTaskDisplayFormatter.create(task, progress);
        graphics.drawString(
            font,
            text, x + width - 5 - font.width(text), y + 6, QuestScreenTheme.getTaskProgress(),
            false
        );
    }

    public static void drawEntity(GuiGraphics graphics, int x, int y, int size, Entity entity) {
        y += (int) (size / 2f);
        x += (int) (size / 4f);
        Minecraft mc = Minecraft.getInstance();
        float scaledSize = 25 / (Math.max(entity.getBbWidth(), entity.getBbHeight()));
        if (Math.abs(entity.getBbHeight() - entity.getBbWidth()) > 5) {
            scaledSize *= 1.2f;
        } else if (Math.abs(entity.getBbHeight() - entity.getBbWidth()) == 0) {
            scaledSize *= 0.8f;
        }

        scaledSize *= size / 40f;

        x += (int) (size / 40f < 1 ? -6 * size / 40f : 5 * size / 40f);
        y += (int) (size / 40f < 1 ? -6 * size / 40f : 8 * size / 35f);

        float rot = -45f;
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
            int offset = (size - scale * 16) / 2;
            try (var pose = new CloseablePoseStack(graphics)) {
                pose.translate(x + offset, y + offset, 0);
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
}
