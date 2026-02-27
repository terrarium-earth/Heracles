package earth.terrarium.heracles.client.components.quests;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestDisplayStatus;
import earth.terrarium.heracles.client.HeraclesClient;
import earth.terrarium.heracles.client.components.base.BaseParentWidget;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.ui.QuestTab;
import earth.terrarium.heracles.common.menus.quests.QuestsContent;
import earth.terrarium.heracles.common.utils.ModUtils;
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

    private static final ResourceLocation ARROW = new ResourceLocation(Heracles.MOD_ID, "textures/gui/arrow.png");

    private final String group;
    private final QuestsContent content;
    private final QuestActionHandler handler;

    private final Map<String, ClientQuests.QuestEntry> quests = new HashMap<>();

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

        int minX = IntStream.of(xs).min().orElse(0) - 100;
        int minY = IntStream.of(ys).min().orElse(0) - 100;
        int maxX = IntStream.of(xs).max().orElse(0) + 100;
        int maxY = IntStream.of(ys).max().orElse(0) + 100;

        if (isEditing) {
            BOUNDS.setBounds();
        } else {
            if (center) {
                BOUNDS.center(minX, minY, maxX, maxY);
            }
            BOUNDS.setBounds(minX, minY, maxX, maxY);
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
        List<QuestWidget> widgets = Util.make(new ArrayList<>(), list -> this.visit(QuestWidget.class, list::add));
        setPositions(widgets);
        renderArrows(graphics, widgets);
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        graphics.disableScissor();
    }

    private void setPositions(List<QuestWidget> widgets) {
        int x = this.getX() + this.getWidth() / 2 + BOUNDS.x();
        int y = this.getY() + this.getHeight() / 2 + BOUNDS.y();
        widgets.forEach(widget -> widget.updatePosition(x, y));
    }

    public Vector2i toLocal(double mouseX, double mouseY) {
        return new Vector2i(
            (int) (mouseX - this.getX() - this.getWidth() / 2 - BOUNDS.x()),
            (int) (mouseY - this.getY() - this.getHeight() / 2 - BOUNDS.y())
        );
    }

    private void renderArrows(GuiGraphics graphics, List<QuestWidget> widgets) {
        RenderSystem.setShaderTexture(0, ARROW);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        final Set<Pair<Vector2i, Vector2i>> lines = new HashSet<>();

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

                try (var pose = new CloseablePoseStack(graphics)) {
                    pose.translate(12, 12, 0);
                    pose.translate(widget.getX(), widget.getY(), 0);
                    pose.mulPose(Axis.ZP.rotation((float) Mth.atan2(yDiff, xDiff)));

                    buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                    buffer.vertex(pose.last().pose(), 0, -2, 0).uv(0, 0).endVertex();
                    buffer.vertex(pose.last().pose(), 0, 2, 0).uv(0, 1).endVertex();
                    buffer.vertex(pose.last().pose(), length, 2, 0).uv(length / 3f, 1).endVertex();
                    buffer.vertex(pose.last().pose(), length, -2, 0).uv(length / 3f, 0).endVertex();
                    tesselator.end();
                }
            }
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        if (Screen.hasShiftDown()) {
            BOUNDS.add((int) -scrollAmount * 10, 0);
        } else {
            BOUNDS.add(0, (int) -scrollAmount * 10);
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean clicked = super.mouseClicked(mouseX, mouseY, button);
        BOUNDS.setStart(mouseX, mouseY);
        QuestWidget widget = clicked && this.getFocused() instanceof QuestWidget questWidget ? questWidget : null;
        this.handler.onClick(mouseX, mouseY, button, widget);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        return this.handler.onRelease(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.handler.onDrag(mouseX, mouseY, button, dragX, dragY).isUndefined()) {
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
}
