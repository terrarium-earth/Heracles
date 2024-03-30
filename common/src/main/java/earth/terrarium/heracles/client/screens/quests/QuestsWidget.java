package earth.terrarium.heracles.client.screens.quests;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestDisplayStatus;
import earth.terrarium.heracles.client.HeraclesClient;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.screens.AbstractQuestScreen;
import earth.terrarium.heracles.client.screens.mousemode.MouseMode;
import earth.terrarium.heracles.client.utils.ClientUtils;
import earth.terrarium.heracles.client.utils.MouseClick;
import earth.terrarium.heracles.client.utils.TexturePlacements;
import earth.terrarium.heracles.client.widgets.base.BaseWidget;
import earth.terrarium.heracles.common.menus.quests.QuestsContent;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.quests.OpenQuestPacket;
import earth.terrarium.heracles.common.utils.ModUtils;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.joml.Vector2i;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class QuestsWidget extends BaseWidget {

    public static final Vector2i offset = new Vector2i();

    private static final Vector2i MAX = new Vector2i(5000, 5000);
    private static final Vector2i MIN = new Vector2i(-5000, -5000);

    private static final ResourceLocation ARROW = new ResourceLocation(Heracles.MOD_ID, "textures/gui/arrow.png");


    private final Set<String> visibleQuests = new HashSet<>();
    private final List<QuestWidget> widgets = new ArrayList<>();
    private final List<ClientQuests.QuestEntry> entries = new ArrayList<>();

    private final int x;
    private final int y;
    private final int fullWidth;
    private final int selectedWidth;
    private int width;
    private final int height;

    private final Vector2i start = new Vector2i();
    private final Vector2i startOffset = new Vector2i();
    private final Vector2i centreOffset = new Vector2i();

    private Vector2i lastClick = null;

    private final SelectQuestHandler selectHandler;

    private final Supplier<MouseMode> mouseMode;
    private final BooleanSupplier inspectorOpened;

    private final String group;

    private QuestsContent content;

    private int maxX = 0;
    private int maxY = 0;
    private int minX = 0;
    private int minY = 0;

    public QuestsWidget(int x, int y, int width, int selectedWidth, int height, BooleanSupplier inspectorOpened, Supplier<MouseMode> mouseMode, Consumer<ClientQuests.QuestEntry> onSelection) {
        this.x = x;
        this.y = y;
        this.fullWidth = width;
        this.selectedWidth = selectedWidth;
        this.width = width;
        this.height = height;
        this.inspectorOpened = inspectorOpened;
        this.mouseMode = mouseMode;
        this.group = ClientUtils.screen() instanceof QuestsScreen screen ? screen.getGroup() : "";
        this.selectHandler = new SelectQuestHandler(this.group, onSelection);
    }

    public void update(QuestsContent content, List<Pair<ClientQuests.QuestEntry, ModUtils.QuestStatus>> quests) {
        this.content = content;
        this.widgets.clear();
        this.entries.clear();
        this.visibleQuests.clear();

        Object2BooleanMap<String> statuses = new Object2BooleanOpenHashMap<>();
        statuses.defaultReturnValue(true);
        quests.forEach(quest -> statuses.put(quest.getFirst().key(), quest.getSecond().isComplete()));

        List<Pair<ClientQuests.QuestEntry, ModUtils.QuestStatus>> visibleQuests = new ArrayList<>();

        boolean isEditing = isEditing();

        for (Pair<ClientQuests.QuestEntry, ModUtils.QuestStatus> quest : quests) {
            if (isEditing || !shouldHide(this.group, statuses, quest.getFirst())) {
                visibleQuests.add(quest);
            }
        }

        for (Pair<ClientQuests.QuestEntry, ModUtils.QuestStatus> quest : visibleQuests) {
            this.widgets.add(new QuestWidget(quest.getFirst(), quest.getSecond()));
            this.entries.add(quest.getFirst());
            this.visibleQuests.add(quest.getFirst().key());
        }

        int[] xs = this.entries.stream()
            .map(ClientQuests.QuestEntry::value)
            .map(Quest::display)
            .map(display -> display.groups().get(this.group))
            .mapToInt(display -> display.position().x).toArray();
        int[] ys = this.entries.stream()
            .map(ClientQuests.QuestEntry::value)
            .map(Quest::display)
            .map(display -> display.groups().get(this.group))
            .mapToInt(display -> display.position().y).toArray();

        this.minX = Arrays.stream(xs).min().orElse(0) - 100;
        this.minY = Arrays.stream(ys).min().orElse(0) - 100;
        this.maxX = Arrays.stream(xs).max().orElse(0) + 100;
        this.maxY = Arrays.stream(ys).max().orElse(0) + 100;
        centreOffset.set(-((maxX + minX) / 2), -((maxY + minY) / 2));

        if (!HeraclesClient.lastGroup.equalsIgnoreCase(content.group())) {
            offset.set(centreOffset);
        }

        if (isEditing) {
            this.minX = MIN.x();
            this.minY = MIN.y();
            this.maxX = MAX.x();
            this.maxY = MAX.y();
            centreOffset.set(0, 0);
        }
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

    public void addQuest(ClientQuests.QuestEntry quest) {
        for (QuestWidget widget : this.widgets) {
            if (widget.id().equals(quest.key())) {
                return;
            }
        }
        this.widgets.add(new QuestWidget(quest, ModUtils.QuestStatus.IN_PROGRESS));
        this.entries.add(quest);
        if (this.content != null) {
            this.content.quests().put(quest.key(), ModUtils.QuestStatus.IN_PROGRESS);
        }
    }

    public void removeQuest(ClientQuests.QuestEntry quest) {
        this.widgets.removeIf(widget -> widget.id().equals(quest.key()));
        this.entries.removeIf(entry -> entry.key().equals(quest.key()));
        QuestWidget questWidget = this.selectHandler.selectedQuest();
        if (questWidget != null && Objects.equals(this.selectHandler.selectedQuest().id(), quest.key())) {
            this.selectHandler.release();
        }
        if (this.content != null) {
            this.content.quests().remove(quest.key());
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x = this.x;
        int y = this.y;
        this.width = inspectorOpened.getAsBoolean() ? this.selectedWidth : this.fullWidth;

        try (var scissor = RenderUtils.createScissor(Minecraft.getInstance(), graphics, x, y, width, height)) {
            x += this.fullWidth / 2;
            y += this.height / 2;
            RenderSystem.setShaderTexture(0, ARROW);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.enableBlend();

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.getBuilder();

            final Set<Pair<Vector2i, Vector2i>> lines = new HashSet<>();

            for (ClientQuests.QuestEntry entry : this.entries) {
                var position = entry.value().display().position(this.group);

                boolean isHovered = isMouseOver(mouseX, mouseY) &&
                                    mouseX >= x + offset.x() + position.x() &&
                                    mouseX <= x + offset.x() + position.x() + 32 &&
                                    mouseY >= y + offset.y() + position.y() &&
                                    mouseY <= y + offset.y() + position.y() + 32;

                RenderSystem.setShaderColor(0.9F, 0.9F, 0.9F, isHovered ? 0.8f : 0.4F);

                for (ClientQuests.QuestEntry child : entry.dependents()) {
                    if (!child.value().display().groups().containsKey(this.group)) continue;
                    if (!this.visibleQuests.contains(child.key())) continue;
                    if (!child.value().settings().showDependencyArrow()) continue;
                    var childPosition = child.value().display().position(this.group);

                    if (lines.contains(new Pair<>(position, childPosition))) continue;
                    lines.add(new Pair<>(position, childPosition));

                    float px = position.x() + 10f;
                    float py = position.y() + 10f;
                    float cx = childPosition.x() + 10f;
                    float cy = childPosition.y() + 10f;

                    float length = Mth.sqrt(Mth.square(cx - px) + Mth.square(cy - py));

                    try (var pose = new CloseablePoseStack(graphics)) {
                        pose.translate(px + 2, py + 2, 0);
                        pose.translate(x + offset.x(), y + offset.y(), 0);
                        pose.mulPose(Axis.ZP.rotation((float) Mth.atan2(cy - py, cx - px)));

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

            for (QuestWidget widget : this.widgets) {
                widget.render(graphics, scissor.stack(), x + offset.x(), y + offset.y(), mouseX, mouseY, isMouseOver(mouseX, mouseY), partialTick);
                if (mouseMode.get().canSelect() && widget == this.selectHandler.selectedQuest()) {
                    TexturePlacements.Info info = widget.getTextureInfo();
                    graphics.renderOutline(
                        x + offset.x() + widget.x() + info.xOffset() - 2, y + offset.y() + widget.y() + info.yOffset() - 2,
                        info.width() + 4, info.height() + 4,
                        0xFFA8EFF0
                    );
                }
            }
        }

        try (var pose = new CloseablePoseStack(graphics)) {
            pose.translate(0, 0, 300);
            int xFromCentre = centreOffset.x - offset.x;
            if (xFromCentre > this.width / 4 || xFromCentre < -this.width / 4) {
                int canvasWidth = this.maxX - this.minX;
                int width = (this.width - 10) * (this.width - 10) / (canvasWidth + this.width - 10);
                int barX = this.x + 5 + (this.width - 10 - width) / 2 + (this.width - 10 - width) * xFromCentre / canvasWidth;
                graphics.blitNineSliced(AbstractQuestScreen.HEADING, barX, this.y + this.height - 4, width, 2, 2, 32, 2, 224, 126);
            }

            int yFromCentre = centreOffset.y - offset.y;
            if (yFromCentre > this.height / 4 || yFromCentre < -this.height / 4) {
                int canvasHeight = this.maxY - this.minY;
                int height = (this.height - 10) * (this.height - 10) / (canvasHeight + this.height - 10);
                int barY = this.y + 5 + (this.height - 10 - height) / 2 + (this.height - 10 - height) * yFromCentre / canvasHeight;
                graphics.blitNineSliced(AbstractQuestScreen.HEADING, this.x + this.width - 4, barY, 2, height, 2, 2, 32, 222, 96);
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        if (Screen.hasShiftDown()) {
            offset.add((int) scrollAmount * 10, 0);
        } else {
            offset.add(0, (int) scrollAmount * 10);
        }
        offset.set(-Mth.clamp(-offset.x(), minX, maxX), -Mth.clamp(-offset.y(), minY, maxY)); // Flip offset to use bounds properly (fix offset itself eventually)
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        MouseMode mode = this.mouseMode.get();
        lastClick = new Vector2i((int) mouseX, (int) mouseY);
        if (isMouseOver(mouseX, mouseY)) {
            if (mode.canSelect() || mode.canOpen()) {
                for (QuestWidget widget : this.widgets) {
                    if (widget.isMouseOver(mouseX - (this.x + (this.fullWidth / 2f) + offset.x()), mouseY - (this.y + (this.height / 2f) + offset.y()))) {
                        if (mode.canSelect()) {
                            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                            this.selectHandler.clickQuest(mode, (int) mouseX, (int) mouseY, widget);
                        } else if (mode.canOpen()) {
                            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                            NetworkHandler.CHANNEL.sendToServer(new OpenQuestPacket(this.group, widget.id()));
                        }
                        return true;
                    }
                }
            }
            this.selectHandler.release();
            start.set((int) mouseX, (int) mouseY);
            startOffset.set(offset.x(), offset.y());
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 1) return false;
        if (!isMouseOver(mouseX, mouseY)) return false;
        if (lastClick == null || !isMouseOver(lastClick.x(), lastClick.y())) return false;
        MouseMode mode = this.mouseMode.get();
        if (mode.canDrag() || button == InputConstants.MOUSE_BUTTON_MIDDLE) {
            int newX = (int) (mouseX - start.x() + startOffset.x());
            int newY = (int) (mouseY - start.y() + startOffset.y());
            offset.set(-Mth.clamp(-newX, minX, maxX), -Mth.clamp(-newY, minY, maxY)); // Flip offset to use bounds properly (fix offset itself eventually)
        } else if (mode.canDragSelection()) {
            this.selectHandler.onDrag((int) mouseX, (int) mouseY);
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        MouseMode mode = this.mouseMode.get();
        if (mode.canSelect() && this.selectHandler.onKeyPress(keyCode)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height;
    }

    public SelectQuestHandler selectHandler() {
        return this.selectHandler;
    }

    public MouseClick getLocal(MouseClick click) {
        int localX = (int) (click.x() - (this.x + (this.fullWidth / 2f) + offset.x()));
        int localY = (int) (click.y() - (this.y + (this.height / 2f) + offset.y()));
        return new MouseClick(localX, localY, click.button());
    }

    public String group() {
        return this.group;
    }

    public boolean isEditing() {
        return ClientUtils.screen() instanceof QuestsEditScreen;
    }
}
