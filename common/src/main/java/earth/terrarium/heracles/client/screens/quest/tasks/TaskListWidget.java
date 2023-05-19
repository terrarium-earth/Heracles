package earth.terrarium.heracles.client.screens.quest.tasks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.client.QuestTaskWidgets;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.screens.AbstractQuestScreen;
import earth.terrarium.heracles.client.screens.quest.HeadingWidget;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class TaskListWidget extends AbstractContainerEventHandler implements Renderable {

    private static final Component IN_PROGRESS = Component.translatable("quest.heracles.in_progress");
    private static final Component COMPLETED = Component.translatable("quest.heracles.completed");

    private final List<MutablePair<QuestTask<?, ?, ?>, DisplayWidget>> widgets = new ArrayList<>();

    private final QuestProgress progress;
    private final String questId;
    private final Quest quest;
    private final Map<String, ModUtils.QuestStatus> quests;
    private final float completion;

    private final int x;
    private final int y;
    private final int width;
    private final int height;

    private double scrollAmount;
    private int lastFullHeight;

    private MouseClick mouse = null;

    private BiConsumer<QuestTask<?, ?, ?>, Boolean> onClick;

    public TaskListWidget(int x, int y, int width, int height, String questId, Quest quest, QuestProgress progress, Map<String, ModUtils.QuestStatus> quests) {
        this.progress = progress;
        this.completion = progress.isComplete() ? 1 : calculationCompletion(quest, progress);
        this.quests = quests;
        this.questId = questId;
        this.quest = quest;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.lastFullHeight = this.height;
    }

    public void update(Collection<QuestTask<?, ?, ?>> tasks) {
        List<MutablePair<QuestTask<?, ?, ?>, DisplayWidget>> dependencies = new ArrayList<>();
        List<MutablePair<QuestTask<?, ?, ?>, DisplayWidget>> inProgress = new ArrayList<>();
        List<MutablePair<QuestTask<?, ?, ?>, DisplayWidget>> completed = new ArrayList<>();
        for (var task : tasks) {
            TaskProgress<?> taskProgress = this.progress.getTask(task);
            DisplayWidget widget = QuestTaskWidgets.create(ModUtils.cast(task), taskProgress);
            if (widget != null) {
                if (taskProgress.isComplete()) {
                    completed.add(new MutablePair<>(task, widget));
                } else {
                    inProgress.add(new MutablePair<>(task, widget));
                }
            }
        }
        for (String dependency : quest.dependencies()) {
            ModUtils.QuestStatus status = this.quests.get(dependency);
            Quest questDependency = ClientQuests.get(dependency).map(ClientQuests.QuestEntry::value).orElse(null);
            if (status != ModUtils.QuestStatus.COMPLETED && questDependency != null) {
                dependencies.add(new MutablePair<>(null, new DependencyDisplayWidget(questDependency)));
            }
        }
        this.widgets.clear();
        this.widgets.add(new MutablePair<>(null, new TaskListHeadingWidget(this.completion)));
        if (!dependencies.isEmpty()) {
            this.widgets.add(new MutablePair<>(null, new HeadingWidget(Component.literal("Dependencies"), 0xFF000080)));
            this.widgets.addAll(dependencies);
        }
        if (!inProgress.isEmpty()) {
            this.widgets.add(new MutablePair<>(null, new HeadingWidget(IN_PROGRESS, 0xFF5691FF)));
            this.widgets.addAll(inProgress);
        }
        if (!completed.isEmpty()) {
            this.widgets.add(new MutablePair<>(null, new HeadingWidget(COMPLETED, 0xFF04CB40)));
            this.widgets.addAll(completed);
        }
    }

    private static float calculationCompletion(Quest quest, QuestProgress progress) {
        float completion = 0;
        for (var task : quest.tasks().values()) {
            completion += progress.getTask(task).isComplete() ? 1 : 0;
        }
        return completion / quest.tasks().size();
    }

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        int x = this.x;
        int y = this.y;

        int fullHeight = 0;
        Pair<QuestTask<?, ?, ?>, Boolean> clicked = null;

        try (var scissor = RenderUtils.createScissorBoxStack(new ScissorBoxStack(), Minecraft.getInstance(), pose, x, y, width + 30, height)) {
            for (var pair : this.widgets) {
                var widget = pair.right;
                if (this.mouse != null && widget.mouseClicked(this.mouse.x() - x, this.mouse.y() - (y - this.scrollAmount), this.mouse.button(), this.width)) {
                    this.mouse = null;
                }
                widget.render(pose, scissor.stack(), x, y - (int) this.scrollAmount, this.width, mouseX, mouseY, this.isMouseOver(mouseX, mouseY), partialTick);

                var itemheight = widget.getHeight(this.width);

                boolean hovered = mouseX > x && mouseX < x + this.width + 14 && mouseY > y && mouseY < y + itemheight;

                if (hovered && pair.left != null && this.onClick != null) {
                    RenderUtils.bindTexture(AbstractQuestScreen.HEADING);
                    boolean editHovered = mouseX > x + width + 1 && mouseX < x + width + 12 && mouseY > y + 1 && mouseY < y + 12;
                    Gui.blit(pose, x + width + 1, y + 1, 33, editHovered ? 26 : 15, 11, 11, 256, 256);
                    CursorUtils.setCursor(editHovered, CursorScreen.Cursor.POINTER);
                    if (mouse != null && mouse.x() > x + width + 1 && mouse.x() < x + width + 12 && mouse.y() > y + 1 && mouse.y() < y + 12 && mouse.button() == 0) {
                        clicked = Pair.of(pair.left, false);
                    }

                    boolean removeHovered = mouseX > x + width + 1 && mouseX < x + width + 12 && mouseY > y + 13 && mouseY < y + 24;
                    Gui.blit(pose, x + width + 1, y + 13, 11, removeHovered ? 70 : 59, 11, 11, 256, 256);
                    CursorUtils.setCursor(removeHovered, CursorScreen.Cursor.POINTER);
                    if (mouse != null && mouse.x() > x + width + 1 && mouse.x() < x + width + 12 && mouse.y() > y + 13 && mouse.y() < y + 24 && mouse.button() == 0) {
                        clicked = Pair.of(pair.left, true);
                    }
                }

                y += itemheight;
                fullHeight += itemheight;
            }
            if (clicked != null && this.onClick != null) {
                this.onClick.accept(clicked.getLeft(), clicked.getRight());
            }

            this.mouse = null;
            this.lastFullHeight = fullHeight;
        }
        this.scrollAmount = Mth.clamp(this.scrollAmount, 0.0D, Math.max(0, this.lastFullHeight - this.height));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        this.scrollAmount = Mth.clamp(this.scrollAmount - scrollAmount * 10, 0.0D, Math.max(0, this.lastFullHeight - this.height));
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            this.mouse = new MouseClick(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.x && mouseX <= this.x + this.width + 30 && mouseY >= this.y && mouseY <= this.y + this.height;
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return List.of();
    }

    public void setOnClick(BiConsumer<QuestTask<?, ?, ?>, Boolean> onClick) {
        this.onClick = onClick;
    }

    public void updateTask(QuestTask<?, ?, ?> task) {
        for (var pair : this.widgets) {
            if (pair.left != null && pair.left.id().equals(task.id())) {
                var widget = QuestTaskWidgets.create(ModUtils.cast(task), this.progress.getTask(task));
                if (widget != null) {
                    pair.left = task;
                    pair.right = widget;
                }
                break;
            }
        }
        this.quest.tasks().put(task.id(), task);
        ClientQuests.setDirty(this.questId);
        ClientQuests.get(this.questId).ifPresent(entry -> entry.value().tasks().put(task.id(), task));
    }

    public void removeTask(QuestTask<?, ?, ?> task) {
        for (var pair : this.widgets) {
            if (pair.left != null && pair.left.id().equals(task.id())) {
                this.widgets.remove(pair);
                break;
            }
        }
        this.quest.tasks().remove(task.id());
        ClientQuests.setDirty(this.questId);
        ClientQuests.get(this.questId).ifPresent(entry -> entry.value().tasks().remove(task.id()));
    }

    private record MouseClick(double x, double y, int button) {}
}
