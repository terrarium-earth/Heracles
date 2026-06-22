package earth.terrarium.heracles.client.components.quests;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.util.Pair;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.ui.UIConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.*;

public class QuestsMinimap {

    private static final int MINIMAP_WIDTH = 100;
    private static final int MINIMAP_HEIGHT = 66;
    private static final float SCALE = 4.0f;
    private static final int PADDING = 8;
    private static final int BORDER_COLOR_OUTER = 0xFFA8A8A8;
    private static final int BORDER_COLOR_INNER = 0xFF585659;
    private static final int VIEWPORT_COLOR = 0x55FFFFFF;

    // Normalized top-left position of the minimap within the content area (0..1 range)
    private static float relX = Float.NaN;
    private static float relY = Float.NaN;
    private static boolean repositioning = false;
    private static int dragStartMouseX = 0;
    private static int dragStartMouseY = 0;
    // Drag starting position in pixels (top-left of minimap relative to widget origin)
    private static int dragStartPosX = 0;
    private static int dragStartPosY = 0;

    public static boolean isRepositioning() {
        return repositioning;
    }

    public static void startRepositioning() {
        repositioning = true;
    }

    public static void stopRepositioning() {
        repositioning = false;
    }

    public static boolean handleRepositionClick(double mouseX, double mouseY, int widgetX, int widgetY, int widgetWidth, int widgetHeight) {
        if (!repositioning) return false;
        ensureNormalized(widgetWidth, widgetHeight);
        int availableW = Math.max(0, widgetWidth - MINIMAP_WIDTH);
        int availableH = Math.max(0, widgetHeight - MINIMAP_HEIGHT);
        int mapX = widgetX + Math.round(Mth.clamp(relX, 0f, 1f) * availableW);
        int mapY = widgetY + Math.round(Mth.clamp(relY, 0f, 1f) * availableH);
        if (mouseX >= mapX && mouseX <= mapX + MINIMAP_WIDTH && mouseY >= mapY && mouseY <= mapY + MINIMAP_HEIGHT) {
            dragStartMouseX = (int) mouseX;
            dragStartMouseY = (int) mouseY;
            dragStartPosX = mapX - widgetX;
            dragStartPosY = mapY - widgetY;
            return true;
        }
        repositioning = false;
        return false;
    }

    public static boolean handleRepositionDrag(double mouseX, double mouseY, int widgetX, int widgetY, int widgetWidth, int widgetHeight) {
        if (!repositioning) return false;
        ensureNormalized(widgetWidth, widgetHeight);
        int availableW = Math.max(0, widgetWidth - MINIMAP_WIDTH);
        int availableH = Math.max(0, widgetHeight - MINIMAP_HEIGHT);
        int newPosX = Mth.clamp(dragStartPosX + (int) (mouseX - dragStartMouseX), 0, availableW);
        int newPosY = Mth.clamp(dragStartPosY + (int) (mouseY - dragStartMouseY), 0, availableH);
        relX = availableW == 0 ? 0f : (float) newPosX / (float) availableW;
        relY = availableH == 0 ? 0f : (float) newPosY / (float) availableH;
        return true;
    }

    public static boolean handleRepositionRelease() {
        if (!repositioning) return false;
        return true;
    }


    public static void render(
        GuiGraphics graphics,
        int mouseX, int mouseY,
        int widgetX, int widgetY,
        int widgetWidth, int widgetHeight,
        float scale,
        OffsetBounds bounds,
        Collection<QuestWidget> quests,
        int minX, int minY, int maxX, int maxY
    ) {
        ensureNormalized(widgetWidth, widgetHeight);
        int availableW = Math.max(0, widgetWidth - MINIMAP_WIDTH);
        int availableH = Math.max(0, widgetHeight - MINIMAP_HEIGHT);
        int mapX = widgetX + Math.round(Mth.clamp(relX, 0f, 1f) * availableW);
        int mapY = widgetY + Math.round(Mth.clamp(relY, 0f, 1f) * availableH);

        float vWidth = MINIMAP_WIDTH * SCALE;
        float vHeight = MINIMAP_HEIGHT * SCALE;

        graphics.pose().pushPose();
        graphics.pose().translate(mapX, mapY, 200);
        graphics.pose().scale(1.0f / SCALE, 1.0f / SCALE, 1.0f);

        Widgets.frame(frameLayoutLayoutWidget -> {
            frameLayoutLayoutWidget.setPosition(0, 0);
            frameLayoutLayoutWidget.setSize((int) vWidth, (int) vHeight);
            frameLayoutLayoutWidget.withTexture(UIConstants.MODAL);
            frameLayoutLayoutWidget.render(graphics, 0, 0, 0);
        });

        float worldWidth = Math.max(1, maxX - minX);
        float worldHeight = Math.max(1, maxY - minY);

        float mapScale = Math.min(vWidth / worldWidth, vHeight / worldHeight);
        float innerOffsetX = (vWidth - worldWidth * mapScale) / 2f;
        float innerOffsetY = (vHeight - worldHeight * mapScale) / 2f;

        float scaleX = mapScale;
        float scaleY = mapScale;

        Map<String, Vector2f> questPositions = new HashMap<>();
        for (QuestWidget widget : quests) {
            Vector2i pos = widget.position();

            float dotX = innerOffsetX + (pos.x() - minX) * scaleX;
            float dotY = innerOffsetY + (pos.y() - minY) * scaleY;

            dotX = Mth.clamp(dotX, 0f, vWidth - 16f);
            dotY = Mth.clamp(dotY, 0f, vHeight - 16f);
            questPositions.put(widget.entry().key(), new Vector2f(dotX, dotY));
        }

        Set<Pair<String, String>> lines = new HashSet<>();
        boolean empty = true;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        var matrix = graphics.pose().last().pose();
        for (QuestWidget widget : quests) {
            Vector2f start = questPositions.get(widget.entry().key());
            if (start == null) continue;
            for (var dependent : widget.entry().dependents()) {
                if (!questPositions.containsKey(dependent.key())) continue;
                if (!dependent.value().settings().showDependencyArrow()) continue;
                if (lines.add(new Pair<>(widget.entry().key(), dependent.key()))) {
                    Vector2f end = questPositions.get(dependent.key());
                    float dx = end.x() - start.x();
                    float dy = end.y() - start.y();
                    float len = (float) Math.sqrt(dx * dx + dy * dy);
                    if (len > 0.1f) {
                        empty = false;
                        float nx = -dy / len * 1.0f;
                        float ny = dx / len * 1.0f;
                        buffer.addVertex(matrix, start.x() + 8f - nx, start.y() + 8f - ny, 0).setColor(1.0f, 1.0f, 1.0f, 1.0f);
                        buffer.addVertex(matrix, start.x() + 8f + nx, start.y() + 8f + ny, 0).setColor(1.0f, 1.0f, 1.0f, 1.0f);
                        buffer.addVertex(matrix, end.x() + 8f + nx, end.y() + 8f + ny, 0).setColor(1.0f, 1.0f, 1.0f, 1.0f);
                        buffer.addVertex(matrix, end.x() + 8f - nx, end.y() + 8f - ny, 0).setColor(1.0f, 1.0f, 1.0f, 1.0f);
                    }
                }
            }
        }
        if (!empty) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableDepthTest();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            BufferUploader.drawWithShader(buffer.buildOrThrow());
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
        }

        float vMouseX = (mouseX - mapX) * SCALE;
        float vMouseY = (mouseY - mapY) * SCALE;

        QuestWidget hovered = null;
        for (QuestWidget widget : quests) {
            Vector2f pos = questPositions.get(widget.entry().key());
            float dotX = pos.x();
            float dotY = pos.y();

            int color = switch (widget.status()) {
                case COMPLETED_CLAIMED -> 0xFF00FF00;
                case COMPLETED -> 0xFFFFAA00;
                case IN_PROGRESS -> 0xFFFFFF00;
                case LOCKED -> 0xFFFF0000;
            };

            if (widget.isSelected()) {
                graphics.renderOutline((int) dotX - 2, (int) dotY - 2, 20, 20, 0xFFFFFFFF);
            }

            graphics.fill((int) dotX, (int) dotY, (int) dotX + 16, (int) dotY + 16, color);
            widget.quest.display().icon().render(graphics, (int) dotX, (int) dotY, 16, 16);

            if (vMouseX >= dotX && vMouseX <= dotX + 16 && vMouseY >= dotY && vMouseY <= dotY + 16) {
                hovered = widget;
            }
        }

        float vpWorldWidth = widgetWidth / scale;
        float vpWorldHeight = widgetHeight / scale;
        float vpWorldLeft = -bounds.x() - vpWorldWidth / 2f;
        float vpWorldTop = -bounds.y() - vpWorldHeight / 2f;
        float vpWorldRight = vpWorldLeft + vpWorldWidth;
        float vpWorldBottom = vpWorldTop + vpWorldHeight;

        int vpMinX = (int) (innerOffsetX + (vpWorldLeft - minX) * scaleX);
        int vpMinY = (int) (innerOffsetY + (vpWorldTop - minY) * scaleY);
        int vpMaxX = (int) (innerOffsetX + (vpWorldRight - minX) * scaleX);
        int vpMaxY = (int) (innerOffsetY + (vpWorldBottom - minY) * scaleY);

        vpMinX = Mth.clamp(vpMinX, 0, (int) vWidth);
        vpMinY = Mth.clamp(vpMinY, 0, (int) vHeight);
        vpMaxX = Mth.clamp(vpMaxX, 0, (int) vWidth);
        vpMaxY = Mth.clamp(vpMaxY, 0, (int) vHeight);

        graphics.fill(vpMinX, vpMinY, vpMaxX, vpMaxY, VIEWPORT_COLOR);
        graphics.renderOutline(vpMinX, vpMinY, vpMaxX - vpMinX, vpMaxY - vpMinY, 0xCCFFFFFF);

        graphics.pose().popPose();

        // Draw border outside scaled context so it has consistent 1px thickness
        graphics.pose().pushPose();
        graphics.pose().translate(mapX, mapY, 201);
        graphics.renderOutline(0, 0, MINIMAP_WIDTH, MINIMAP_HEIGHT, BORDER_COLOR_OUTER);
        graphics.renderOutline(1, 1, MINIMAP_WIDTH - 2, MINIMAP_HEIGHT - 2, BORDER_COLOR_INNER);
        graphics.pose().popPose();

        if (hovered != null) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(hovered.quest.display().title());
            Component subtitle = hovered.quest.display().subtitle();
            if (subtitle != null && !subtitle.getString().isBlank()) {
                tooltip.add(subtitle);
            }
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 500);
            graphics.renderComponentTooltip(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
            graphics.pose().popPose();
        }
    }

    public static boolean isMouseOver(
        double mouseX, double mouseY,
        int widgetX, int widgetY,
        int widgetWidth, int widgetHeight
    ) {
        ensureNormalized(widgetWidth, widgetHeight);
        int availableW = Math.max(0, widgetWidth - MINIMAP_WIDTH);
        int availableH = Math.max(0, widgetHeight - MINIMAP_HEIGHT);
        int mapX = widgetX + Math.round(Mth.clamp(relX, 0f, 1f) * availableW);
        int mapY = widgetY + Math.round(Mth.clamp(relY, 0f, 1f) * availableH);
        return mouseX >= mapX && mouseX <= mapX + MINIMAP_WIDTH && mouseY >= mapY && mouseY <= mapY + MINIMAP_HEIGHT;
    }

    public static boolean handleClick(
        double mouseX, double mouseY,
        int widgetX, int widgetY,
        int widgetWidth, int widgetHeight,
        OffsetBounds bounds,
        int minX, int minY, int maxX, int maxY
    ) {
        ensureNormalized(widgetWidth, widgetHeight);
        int availableW = Math.max(0, widgetWidth - MINIMAP_WIDTH);
        int availableH = Math.max(0, widgetHeight - MINIMAP_HEIGHT);
        int mapX = widgetX + Math.round(Mth.clamp(relX, 0f, 1f) * availableW);
        int mapY = widgetY + Math.round(Mth.clamp(relY, 0f, 1f) * availableH);

        if (mouseX >= mapX && mouseX <= mapX + MINIMAP_WIDTH && mouseY >= mapY && mouseY <= mapY + MINIMAP_HEIGHT) {
            float worldWidth = Math.max(1, maxX - minX);
            float worldHeight = Math.max(1, maxY - minY);

            float mapScale = Math.min((float) MINIMAP_WIDTH / worldWidth, (float) MINIMAP_HEIGHT / worldHeight);
            float innerOffsetX = (MINIMAP_WIDTH - worldWidth * mapScale) / 2f;
            float innerOffsetY = (MINIMAP_HEIGHT - worldHeight * mapScale) / 2f;

            float worldX = (float) (mouseX - mapX - innerOffsetX) / mapScale + minX;
            float worldY = (float) (mouseY - mapY - innerOffsetY) / mapScale + minY;

            bounds.add((int) (-worldX - bounds.x()), (int) (-worldY - bounds.y()));
            return true;
        }
        return false;
    }

    public static boolean handleDrag(
        double mouseX, double mouseY,
        int widgetX, int widgetY,
        int widgetWidth, int widgetHeight,
        OffsetBounds bounds,
        int minX, int minY, int maxX, int maxY
    ) {
        ensureNormalized(widgetWidth, widgetHeight);
        int availableW = Math.max(0, widgetWidth - MINIMAP_WIDTH);
        int availableH = Math.max(0, widgetHeight - MINIMAP_HEIGHT);
        int mapX = widgetX + Math.round(Mth.clamp(relX, 0f, 1f) * availableW);
        int mapY = widgetY + Math.round(Mth.clamp(relY, 0f, 1f) * availableH);

        if (mouseX >= mapX && mouseX <= mapX + MINIMAP_WIDTH && mouseY >= mapY && mouseY <= mapY + MINIMAP_HEIGHT) {
            float worldWidth = Math.max(1, maxX - minX);
            float worldHeight = Math.max(1, maxY - minY);

            float mapScale = Math.min((float) MINIMAP_WIDTH / worldWidth, (float) MINIMAP_HEIGHT / worldHeight);
            float innerOffsetX = (MINIMAP_WIDTH - worldWidth * mapScale) / 2f;
            float innerOffsetY = (MINIMAP_HEIGHT - worldHeight * mapScale) / 2f;

            float worldX = (float) (mouseX - mapX - innerOffsetX) / mapScale + minX;
            float worldY = (float) (mouseY - mapY - innerOffsetY) / mapScale + minY;

            bounds.setStart((int) (-worldX - bounds.x()), (int) (-worldY - bounds.y()));
            return true;
        }

        return false;
    };
    public static int getMinimapWidth() {
        return MINIMAP_WIDTH;
    }

    public static int getMinimapHeight() {
        return MINIMAP_HEIGHT;
    }

    public static void renderDocked(
        GuiGraphics graphics,
        int mouseX, int mouseY,
        int dockedX, int dockedY,
        int dockedWidth, int dockedHeight,
        float scale,
        OffsetBounds bounds,
        Collection<QuestWidget> quests,
        int widgetWidth, int widgetHeight,
        int minX, int minY, int maxX, int maxY
    ) {
        int mapX = dockedX;
        int mapY = dockedY;
        int mapW = dockedWidth;
        int mapH = dockedHeight;

        float vWidth = mapW * SCALE;
        float vHeight = mapH * SCALE;

        graphics.pose().pushPose();
        graphics.pose().translate(mapX, mapY, 200);
        graphics.pose().scale(1.0f / SCALE, 1.0f / SCALE, 1.0f);

        Widgets.frame(frameLayoutLayoutWidget -> {
            frameLayoutLayoutWidget.setPosition(0, 0);
            frameLayoutLayoutWidget.setSize((int) vWidth, (int) vHeight);
            frameLayoutLayoutWidget.withTexture(UIConstants.MODAL);
            frameLayoutLayoutWidget.render(graphics, 0, 0, 0);
        });

        float worldWidth = Math.max(1, maxX - minX);
        float worldHeight = Math.max(1, maxY - minY);

        float mapScale = Math.min(vWidth / worldWidth, vHeight / worldHeight);
        float innerOffsetX = (vWidth - worldWidth * mapScale) / 2f;
        float innerOffsetY = (vHeight - worldHeight * mapScale) / 2f;

        float scaleX = mapScale;
        float scaleY = mapScale;

        Map<String, Vector2f> questPositions = new HashMap<>();
        for (QuestWidget widget : quests) {
            Vector2i pos = widget.position();

            float dotX = innerOffsetX + (pos.x() - minX) * scaleX;
            float dotY = innerOffsetY + (pos.y() - minY) * scaleY;

            dotX = Mth.clamp(dotX, 0f, vWidth - 16f);
            dotY = Mth.clamp(dotY, 0f, vHeight - 16f);
            questPositions.put(widget.entry().key(), new Vector2f(dotX, dotY));
        }

        Set<Pair<String, String>> lines = new HashSet<>();
        boolean empty = true;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        var matrix = graphics.pose().last().pose();
        for (QuestWidget widget : quests) {
            Vector2f start = questPositions.get(widget.entry().key());
            if (start == null) continue;
            for (var dependent : widget.entry().dependents()) {
                if (!questPositions.containsKey(dependent.key())) continue;
                if (!dependent.value().settings().showDependencyArrow()) continue;
                if (lines.add(new Pair<>(widget.entry().key(), dependent.key()))) {
                    Vector2f end = questPositions.get(dependent.key());
                    float dx = end.x() - start.x();
                    float dy = end.y() - start.y();
                    float len = (float) Math.sqrt(dx * dx + dy * dy);
                    if (len > 0.1f) {
                        empty = false;
                        float nx = -dy / len * 1.0f;
                        float ny = dx / len * 1.0f;
                        buffer.addVertex(matrix, start.x() + 8f - nx, start.y() + 8f - ny, 0).setColor(1.0f, 1.0f, 1.0f, 1.0f);
                        buffer.addVertex(matrix, start.x() + 8f + nx, start.y() + 8f + ny, 0).setColor(1.0f, 1.0f, 1.0f, 1.0f);
                        buffer.addVertex(matrix, end.x() + 8f + nx, end.y() + 8f + ny, 0).setColor(1.0f, 1.0f, 1.0f, 1.0f);
                        buffer.addVertex(matrix, end.x() + 8f - nx, end.y() + 8f - ny, 0).setColor(1.0f, 1.0f, 1.0f, 1.0f);
                    }
                }
            }
        }
        if (!empty) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableDepthTest();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            BufferUploader.drawWithShader(buffer.buildOrThrow());
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
        }

        float vMouseX = (mouseX - mapX) * SCALE;
        float vMouseY = (mouseY - mapY) * SCALE;

        QuestWidget hovered = null;
        for (QuestWidget widget : quests) {
            Vector2f pos = questPositions.get(widget.entry().key());
            float dotX = pos.x();
            float dotY = pos.y();

            int color = switch (widget.status()) {
                case COMPLETED -> 0xFF00FF00;
                case IN_PROGRESS -> 0xFFFFFF00;
                case LOCKED -> 0xFFFF0000;
                default -> 0xFFAAAAAA;
            };

            if (widget.isSelected()) {
                graphics.renderOutline((int) dotX - 2, (int) dotY - 2, 20, 20, 0xFFFFFFFF);
            }

            graphics.fill((int) dotX, (int) dotY, (int) dotX + 16, (int) dotY + 16, color);
            widget.quest.display().icon().render(graphics, (int) dotX, (int) dotY, 16, 16);

            if (vMouseX >= dotX && vMouseX <= dotX + 16 && vMouseY >= dotY && vMouseY <= dotY + 16) {
                hovered = widget;
            }
        }

        float vpWorldWidth = widgetWidth / scale;
        float vpWorldHeight = widgetHeight / scale;
        float vpWorldLeft = -bounds.x() - vpWorldWidth / 2f;
        float vpWorldTop = -bounds.y() - vpWorldHeight / 2f;
        float vpWorldRight = vpWorldLeft + vpWorldWidth;
        float vpWorldBottom = vpWorldTop + vpWorldHeight;

        int vpMinX = (int) (innerOffsetX + (vpWorldLeft - minX) * scaleX);
        int vpMinY = (int) (innerOffsetY + (vpWorldTop - minY) * scaleY);
        int vpMaxX = (int) (innerOffsetX + (vpWorldRight - minX) * scaleX);
        int vpMaxY = (int) (innerOffsetY + (vpWorldBottom - minY) * scaleY);

        vpMinX = Mth.clamp(vpMinX, 0, (int) vWidth);
        vpMinY = Mth.clamp(vpMinY, 0, (int) vHeight);
        vpMaxX = Mth.clamp(vpMaxX, 0, (int) vWidth);
        vpMaxY = Mth.clamp(vpMaxY, 0, (int) vHeight);

        graphics.fill(vpMinX, vpMinY, vpMaxX, vpMaxY, VIEWPORT_COLOR);
        graphics.renderOutline(vpMinX, vpMinY, vpMaxX - vpMinX, vpMaxY - vpMinY, 0xCCFFFFFF);

        graphics.pose().popPose();

        // Draw border outside scaled context
        graphics.pose().pushPose();
        graphics.pose().translate(mapX, mapY, 201);
        graphics.renderOutline(0, 0, mapW, mapH, BORDER_COLOR_OUTER);
        graphics.renderOutline(1, 1, mapW - 2, mapH - 2, BORDER_COLOR_INNER);
        graphics.pose().popPose();

        if (hovered != null) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(hovered.quest.display().title());
            Component subtitle = hovered.quest.display().subtitle();
            if (subtitle != null && !subtitle.getString().isBlank()) {
                tooltip.add(subtitle);
            }
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 500);
            graphics.renderComponentTooltip(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
            graphics.pose().popPose();
        }
    }

    public static boolean handleDockedClick(
        double mouseX, double mouseY,
        int dockedX, int dockedY,
        int dockedWidth, int dockedHeight,
        int widgetWidth, int widgetHeight,
        OffsetBounds bounds,
        int minX, int minY, int maxX, int maxY
    ) {
        if (mouseX >= dockedX && mouseX <= dockedX + dockedWidth && mouseY >= dockedY && mouseY <= dockedY + dockedHeight) {
            float vWidth = dockedWidth * SCALE;
            float vHeight = dockedHeight * SCALE;

            float worldWidth = Math.max(1, maxX - minX);
            float worldHeight = Math.max(1, maxY - minY);

            float mapScale = Math.min(vWidth / worldWidth, vHeight / worldHeight);
            float innerOffsetX = (vWidth - worldWidth * mapScale) / 2f;
            float innerOffsetY = (vHeight - worldHeight * mapScale) / 2f;

            float worldX = (float) ((mouseX - dockedX) * SCALE - innerOffsetX) / mapScale + minX;
            float worldY = (float) ((mouseY - dockedY) * SCALE - innerOffsetY) / mapScale + minY;

            bounds.add((int) (-worldX - bounds.x()), (int) (-worldY - bounds.y()));
            return true;
        }
        return false;
    }

    public static boolean handleDockedDrag(
        double mouseX, double mouseY,
        int dockedX, int dockedY,
        int dockedWidth, int dockedHeight,
        int widgetWidth, int widgetHeight,
        OffsetBounds bounds,
        int minX, int minY, int maxX, int maxY
    ) {
        if (mouseX >= dockedX && mouseX <= dockedX + dockedWidth && mouseY >= dockedY && mouseY <= dockedY + dockedHeight) {
            float vWidth = dockedWidth * SCALE;
            float vHeight = dockedHeight * SCALE;

            float worldWidth = Math.max(1, maxX - minX);
            float worldHeight = Math.max(1, maxY - minY);

            float mapScale = Math.min(vWidth / worldWidth, vHeight / worldHeight);
            float innerOffsetX = (vWidth - worldWidth * mapScale) / 2f;
            float innerOffsetY = (vHeight - worldHeight * mapScale) / 2f;

            float worldX = (float) ((mouseX - dockedX) * SCALE - innerOffsetX) / mapScale + minX;
            float worldY = (float) ((mouseY - dockedY) * SCALE - innerOffsetY) / mapScale + minY;

            bounds.setStart((int) (-worldX - bounds.x()), (int) (-worldY - bounds.y()));
            return true;
        }
        return false;
    }

    public static boolean isMouseOverDocked(
        double mouseX, double mouseY,
        int dockedX, int dockedY,
        int dockedWidth, int dockedHeight
    ) {
        return mouseX >= dockedX && mouseX <= dockedX + dockedWidth && mouseY >= dockedY && mouseY <= dockedY + dockedHeight;
    }

    private static void ensureNormalized(int widgetWidth, int widgetHeight) {
        int availableW = Math.max(1, widgetWidth - MINIMAP_WIDTH);
        int availableH = Math.max(1, widgetHeight - MINIMAP_HEIGHT);
        if (Float.isNaN(relX)) {
            relX = Mth.clamp((availableW - PADDING) / (float) availableW, 0f, 1f);
        }
        if (Float.isNaN(relY)) {
            relY = Mth.clamp((availableH - PADDING) / (float) availableH, 0f, 1f);
        }
    }
}
