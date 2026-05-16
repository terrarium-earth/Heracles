package earth.terrarium.heracles.client.components.quests;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestDisplayStatus;
import earth.terrarium.heracles.client.HeraclesClient;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.handlers.DisplayConfig;
import earth.terrarium.heracles.client.ui.QuestTab;
import earth.terrarium.heracles.client.utils.BackgroundTextureManager;
import earth.terrarium.heracles.common.handlers.quests.GroupSettings;
import earth.terrarium.heracles.common.menus.quests.QuestsContent;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.utils.ModUtils;
import earth.terrarium.olympus.client.components.base.BaseParentWidget;
import earth.terrarium.olympus.client.ui.context.ContextMenu;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Vector2i;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class QuestsWidget extends BaseParentWidget {

    private static final OffsetBounds BOUNDS = new OffsetBounds();

    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 2.0f;
    private static final float SCALE_STEP = 0.1f;
    private static float scale = 1.0f;

    private static final int GRID_CELL_SIZE = 27;
    private static final int GRID_COLOR = 0x22FFFFFF;

    private static final ResourceLocation ARROW = Heracles.id("textures/gui/arrow.png");

    private final String group;
    private QuestsContent content;
    private final QuestActionHandler handler;

    private final Map<String, ClientQuests.QuestEntry> quests = new HashMap<>();

    private int minX, minY, maxX, maxY;
    private int dockedMinimapX, dockedMinimapY, dockedMinimapWidth, dockedMinimapHeight;

    public QuestsWidget(int width, int height, QuestsContent content, QuestActionHandler handler) {
        super(width, height);
        this.handler = handler;
        this.group = content.group();
        this.content = content;
    }

    public void update(List<Pair<ClientQuests.QuestEntry, ModUtils.QuestStatus>> quests) {
        this.clear();
        this.quests.clear();

        Object2BooleanMap<String> statuses = new Object2BooleanOpenHashMap<>();
        statuses.defaultReturnValue(true);
        quests.forEach(quest -> statuses.put(quest.getFirst().key(), quest.getSecond().isComplete()));

        List<Pair<ClientQuests.QuestEntry, ModUtils.QuestStatus>> visibleQuests = new ArrayList<>();

        boolean isEditing = QuestTab.isEditing();

        for (Pair<ClientQuests.QuestEntry, ModUtils.QuestStatus> quest : quests) {
            if (isEditing || !shouldHide(this.group, statuses, quest.getFirst())) {
                visibleQuests.add(quest);
            }
        }

        for (Pair<ClientQuests.QuestEntry, ModUtils.QuestStatus> quest : visibleQuests) {
            this.addRenderableWidget(new QuestWidget(this.content.group(), quest.getFirst(), quest.getSecond()));
            this.quests.put(quest.getFirst().key(), quest.getFirst());
        }

        int[] xs = this.quests.values().stream()
            .map(ClientQuests.QuestEntry::value)
            .map(Quest::display)
            .map(display -> display.groups().get(this.group))
            .mapToInt(display -> display.position().x).toArray();
        int[] ys = this.quests.values().stream()
            .map(ClientQuests.QuestEntry::value)
            .map(Quest::display)
            .map(display -> display.groups().get(this.group))
            .mapToInt(display -> display.position().y).toArray();

        boolean center = !HeraclesClient.lastGroup.equalsIgnoreCase(content.group());

        this.minX = IntStream.of(xs).min().orElse(0) - 100;
        this.minY = IntStream.of(ys).min().orElse(0) - 100;
        this.maxX = IntStream.of(xs).max().orElse(0) + 100;
        this.maxY = IntStream.of(ys).max().orElse(0) + 100;

        if (isEditing) {
            BOUNDS.setBounds();
        } else {
            if (center) {
                BOUNDS.center(this.minX, this.minY, this.maxX, this.maxY);
            }
            BOUNDS.setBounds();
        }

        HeraclesClient.lastGroup = content.group();
    }

    private static boolean shouldHide(String group, Object2BooleanMap<String> statuses, ClientQuests.QuestEntry quest) {
        var value = quest.value();
        boolean inGroup = value.display().groups().containsKey(group);
        if (!inGroup) return true;
        return shouldHide(statuses, quest, value.settings().hiddenUntil());
    }

    private static boolean shouldHide(Object2BooleanMap<String> statuses, ClientQuests.QuestEntry quest, QuestDisplayStatus status) {
        if (status == QuestDisplayStatus.COMPLETED) {
            return !statuses.getBoolean(quest.key());
        } else if (status == QuestDisplayStatus.IN_PROGRESS) {
            for (var dependency : quest.dependencies()) {
                if (!statuses.getBoolean(dependency.key())) {
                    return true;
                }
            }
        } else if (status == QuestDisplayStatus.DEPENDENCIES_VISIBLE) {
            boolean visible = statuses.getBoolean(quest.key());
            if (visible) {
                return false;
            }
            for (var dependency : quest.dependencies()) {
                if (shouldHide(statuses, dependency, QuestDisplayStatus.DEPENDENCIES_VISIBLE)) {
                    return true;
                }
            }
            return quest.value().settings().hiddenUntil() != QuestDisplayStatus.DEPENDENCIES_VISIBLE;
        }
        return false;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.enableScissor(this.getX(), this.getY(), this.getX() + getWidth(), this.getY() + getHeight());
        renderBackground(graphics);

        float cx = this.getX() + this.getWidth() / 2f;
        float cy = this.getY() + this.getHeight() / 2f;

        graphics.pose().pushPose();
        graphics.pose().translate(cx, cy, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.pose().translate(-cx, -cy, 0);
        if (DisplayConfig.showGrid) {
            renderGrid(graphics);
        }

        List<QuestWidget> widgets = Util.make(new ArrayList<>(), list -> this.visit(QuestWidget.class, list::add));
        setPositions(widgets);
        renderArrows(graphics, widgets);
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        graphics.pose().popPose();

        if (DisplayConfig.showMinimap && !DisplayConfig.dockMinimap) {
            QuestsMinimap.render(
                graphics,
                mouseX, mouseY,
                this.getX(), this.getY(),
                this.getWidth(), this.getHeight(),
                scale,
                BOUNDS,
                widgets,
                this.minX, this.minY, this.maxX, this.maxY
            );
        }

        graphics.disableScissor();

        if (DisplayConfig.showMinimap && DisplayConfig.dockMinimap && dockedMinimapWidth > 0 && dockedMinimapHeight > 0) {
            QuestsMinimap.renderDocked(
                graphics,
                mouseX, mouseY,
                dockedMinimapX, dockedMinimapY,
                dockedMinimapWidth, dockedMinimapHeight,
                scale,
                BOUNDS,
                widgets,
                this.getWidth(), this.getHeight(),
                this.minX, this.minY, this.maxX, this.maxY
            );
        }
        graphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + 2, 0xFF585659);
        graphics.renderOutline(this.getX(), this.getY() + 2, this.getX() + this.getWidth() + this.width - 1, this.getY() + this.getHeight() - 11, 0xFF1E1E1F);
    }

    private void renderGrid(GuiGraphics graphics) {
        // Expand grid bounds by inverse of scale so the grid covers the full visible area even when zoomed out (scale < 1)
        float cx = this.getX() + this.getWidth() / 2f;
        float cy = this.getY() + this.getHeight() / 2f;
        int x0 = (int) Math.floor((this.getX() - cx) / scale + cx);
        int y0 = (int) Math.floor((this.getY() - cy) / scale + cy);
        int x1 = (int) Math.ceil(((this.getX() + this.getWidth()) - cx) / scale + cx);
        int y1 = (int) Math.ceil(((this.getY() + this.getHeight()) - cy) / scale + cy);

        // Calculate the offset so the grid moves with panning
        int centerX = this.getX() + this.getWidth() / 2 + BOUNDS.x();
        int centerY = this.getY() + this.getHeight() / 2 + BOUNDS.y();

        // Calculate the grid origin aligned to the center offset
        // This ensures quest nodes placed at multiples of GRID_CELL_SIZE
        // will sit neatly inside grid cells
        int gridOffsetX = Math.floorMod(centerX - this.getX() + 17, GRID_CELL_SIZE);
        int gridOffsetY = Math.floorMod(centerY - this.getY() + 13, GRID_CELL_SIZE);

        // Compute the first grid line position at or after the expanded x0/y0
        int startGx = x0 + Math.floorMod(this.getX() + gridOffsetX - x0, GRID_CELL_SIZE);
        int startGy = y0 + Math.floorMod(this.getY() + gridOffsetY - y0, GRID_CELL_SIZE);

        // Draw vertical lines
        for (int gx = startGx; gx <= x1; gx += GRID_CELL_SIZE) {
            graphics.fill(gx, y0, gx + 1, y1, GRID_COLOR);
        }

        // Draw horizontal lines
        for (int gy = startGy; gy <= y1; gy += GRID_CELL_SIZE) {
            graphics.fill(x0, gy, x1, gy + 1, GRID_COLOR);
        }
    }

    private void renderBackground(GuiGraphics graphics) {
        GroupSettings settings = ClientQuests.getGroupSettings(this.group);
        String background = settings.background();
        if (background == null || background.isEmpty()) return;

        ResourceLocation texture = BackgroundTextureManager.getTexture(background);
        if (texture == null) return;

        float alpha = settings.backgroundOpacity() / 100.0F;
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        graphics.blit(texture, this.getX(), this.getY(), 0, 0, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight());
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void setPositions(List<QuestWidget> widgets) {
        int x = this.getX() + this.getWidth() / 2 + BOUNDS.x();
        int y = this.getY() + this.getHeight() / 2 + BOUNDS.y();
        widgets.forEach(widget -> widget.updatePosition(x, y));
    }

    private double toScaledX(double mouseX) {
        float cx = this.getX() + this.getWidth() / 2f;
        return (mouseX - cx) / scale + cx;
    }

    private double toScaledY(double mouseY) {
        float cy = this.getY() + this.getHeight() / 2f;
        return (mouseY - cy) / scale + cy;
    }

    public Vector2i toLocal(double mouseX, double mouseY) {
        double localX = toScaledX(mouseX);
        double localY = toScaledY(mouseY);
        return new Vector2i(
            (int) (localX - this.getX() - this.getWidth() / 2 - BOUNDS.x()),
            (int) (localY - this.getY() - this.getHeight() / 2 - BOUNDS.y())
        );
    }

    private void renderArrows(GuiGraphics graphics, List<QuestWidget> widgets) {
        RenderSystem.setShaderTexture(0, ARROW);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();

        if (!widgets.isEmpty()) {
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

            final Set<Pair<Vector2i, Vector2i>> lines = new HashSet<>();
            boolean empty = true;
            for (QuestWidget widget : widgets) {

                ClientQuests.QuestEntry entry = widget.entry;
                var position = entry.value().display().position(this.group);

                RenderSystem.setShaderColor(0.9F, 0.9F, 0.9F, widget.isHovered() ? 0.8f : 0.4F);

                for (ClientQuests.QuestEntry child : entry.dependents()) {
                    if (!child.value().display().groups().containsKey(this.group)) continue;
                    if (!this.quests.containsKey(child.key())) continue;
                    if (!child.value().settings().showDependencyArrow()) continue;
                    var childPosition = child.value().display().position(this.group);

                    if (lines.contains(new Pair<>(position, childPosition))) continue;
                    lines.add(new Pair<>(position, childPosition));

                    float xDiff = childPosition.x() - position.x();
                    float yDiff = childPosition.y() - position.y();

                    float length = Mth.sqrt(Mth.square(xDiff) + Mth.square(yDiff));
                    empty = false;
                    try (var pose = new CloseablePoseStack(graphics)) {
                        pose.translate(12, 12, 0);
                        pose.translate(widget.getX(), widget.getY(), 0);
                        pose.mulPose(Axis.ZP.rotation((float) Mth.atan2(yDiff, xDiff)));

                        buffer.addVertex(pose.last().pose(), 0, -2, 0).setUv(0, 0);
                        buffer.addVertex(pose.last().pose(), 0, 2, 0).setUv(0, 1);
                        buffer.addVertex(pose.last().pose(), length, 2, 0).setUv(length / 3f, 1);
                        buffer.addVertex(pose.last().pose(), length, -2, 0).setUv(length / 3f, 0);
                    }
                }
            }

            if (!empty) {
                BufferUploader.drawWithShader(buffer.buildOrThrow());
            }
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (Screen.hasControlDown()) {
            scale = Mth.clamp(scale + (float) scrollY * SCALE_STEP, MIN_SCALE, MAX_SCALE);
        } else if (Screen.hasShiftDown()) {
            BOUNDS.add((int) scrollY * 10, 0);
        } else {
            BOUNDS.add(0, (int) scrollY * 10);
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (DisplayConfig.showMinimap && DisplayConfig.dockMinimap && dockedMinimapWidth > 0) {
            if (button == 1 && QuestsMinimap.isMouseOverDocked(mouseX, mouseY, dockedMinimapX, dockedMinimapY, dockedMinimapWidth, dockedMinimapHeight)) {
                ContextMenu.open(mouseX, mouseY, menu -> {
                    menu.button(ConstantComponents.Quests.UNDOCK_MINIMAP, () -> {
                        DisplayConfig.dockMinimap = false;
                        DisplayConfig.save();
                    });
                    menu.button(ConstantComponents.Quests.HIDE_MINIMAP, () -> {
                        DisplayConfig.showMinimap = false;
                        DisplayConfig.save();
                    });
                });
                return true;
            }
            if (QuestsMinimap.handleDockedClick(mouseX, mouseY, dockedMinimapX, dockedMinimapY, dockedMinimapWidth, dockedMinimapHeight, this.getWidth(), this.getHeight(), BOUNDS, this.minX, this.minY, this.maxX, this.maxY)) {
                return true;
            }
        }
        if (DisplayConfig.showMinimap && !DisplayConfig.dockMinimap) {
            if (QuestsMinimap.isRepositioning()) {
                if (button == 1 && QuestsMinimap.isMouseOver(mouseX, mouseY, this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
                    ContextMenu.open(mouseX, mouseY, menu -> {
                        menu.button(ConstantComponents.Quests.CONFIRM_POSITION, QuestsMinimap::stopRepositioning);
                        menu.button(ConstantComponents.Quests.HIDE_MINIMAP, () -> {
                            QuestsMinimap.stopRepositioning();
                            DisplayConfig.showMinimap = false;
                            DisplayConfig.save();
                        });
                    });
                    return true;
                }
                if (QuestsMinimap.handleRepositionClick(mouseX, mouseY, this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
                    return true;
                }
            }
            if (button == 1 && QuestsMinimap.isMouseOver(mouseX, mouseY, this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
                ContextMenu.open(mouseX, mouseY, menu -> {
                    menu.button(ConstantComponents.Quests.REPOSITION_MINIMAP, QuestsMinimap::startRepositioning);
                    menu.button(ConstantComponents.Quests.DOCK_MINIMAP, () -> {
                        DisplayConfig.dockMinimap = true;
                        DisplayConfig.save();
                    });
                    menu.button(ConstantComponents.Quests.HIDE_MINIMAP, () -> {
                        DisplayConfig.showMinimap = false;
                        DisplayConfig.save();
                    });
                });
                return true;
            }
            if (!QuestsMinimap.isRepositioning() && QuestsMinimap.handleClick(mouseX, mouseY, this.getX(), this.getY(), this.getWidth(), this.getHeight(), BOUNDS, this.minX, this.minY, this.maxX, this.maxY)) {
                return true;
            }
        }
        double scaledX = toScaledX(mouseX);
        double scaledY = toScaledY(mouseY);
        boolean clicked = super.mouseClicked(scaledX, scaledY, button);
        BOUNDS.setStart(mouseX, mouseY);
        QuestWidget widget = null;
        if (clicked && this.getFocused() instanceof QuestWidget questWidget) {
            widget = questWidget;
        }
        this.handler.onClick(scaledX, scaledY, button, widget);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (DisplayConfig.showMinimap && !DisplayConfig.dockMinimap && QuestsMinimap.handleRepositionRelease()) {
            return true;
        }
        double scaledX = toScaledX(mouseX);
        double scaledY = toScaledY(mouseY);
        super.mouseReleased(scaledX, scaledY, button);
        return this.handler.onRelease(scaledX, scaledY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (DisplayConfig.showMinimap && DisplayConfig.dockMinimap && dockedMinimapWidth > 0) {
            if (QuestsMinimap.handleDockedDrag(mouseX, mouseY, dockedMinimapX, dockedMinimapY, dockedMinimapWidth, dockedMinimapHeight, this.getWidth(), this.getHeight(), BOUNDS, this.minX, this.minY, this.maxX, this.maxY)) {
                return true;
            }
        }
        if (DisplayConfig.showMinimap && !DisplayConfig.dockMinimap && QuestsMinimap.isRepositioning()) {
            if (QuestsMinimap.handleRepositionDrag(mouseX, mouseY, this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
                return true;
            }
        }
        if (DisplayConfig.showMinimap && !DisplayConfig.dockMinimap && QuestsMinimap.handleDrag(mouseX, mouseY, this.getX(), this.getY(), this.getWidth(), this.getHeight(), BOUNDS, this.minX, this.minY, this.maxX, this.maxY)) {
            return true;
        }

        double scaledDragX = dragX / scale;
        double scaledDragY = dragY / scale;
        if (this.handler.onDrag(toScaledX(mouseX), toScaledY(mouseY), button, scaledDragX, scaledDragY).isUndefined()) {
            BOUNDS.drag(mouseX, mouseY);
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.handler.onKeyPressed(keyCode, scanCode, modifiers);
    }

    public String group() {
        return this.group;
    }

    public void select(Predicate<QuestWidget> predicate) {
        visit(QuestWidget.class, questWidget -> questWidget.setSelected(predicate.test(questWidget)));
    }

    public OffsetBounds bounds() {
        return BOUNDS;
    }

    public void remove(ClientQuests.QuestEntry entry) {
        this.removeWidget(value -> value instanceof QuestWidget widget && widget.entry == entry);
        this.quests.remove(entry.key());
    }

    public void setContent(QuestsContent content) {
        this.content = content;
    }

    public void setDockedMinimapBounds(int x, int y, int width, int height) {
        this.dockedMinimapX = x;
        this.dockedMinimapY = y;
        this.dockedMinimapWidth = width;
        this.dockedMinimapHeight = height;
    }
}
