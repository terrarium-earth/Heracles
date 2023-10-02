package earth.terrarium.heracles.client.screens.quest.tasks;

import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.client.QuestTaskWidgets;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.screens.AbstractQuestScreen;
import earth.terrarium.heracles.client.screens.quest.AddDisplayWidget;
import earth.terrarium.heracles.client.screens.quest.HeadingWidget;
import earth.terrarium.heracles.client.utils.MouseClick;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import earth.terrarium.heracles.common.network.packets.quests.data.NetworkQuestData;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;

public class TaskListWidget extends AbstractContainerEventHandler implements Renderable {

    private static final HeadingWidget IN_PROGRESS = new HeadingWidget(Component.translatable("quest.heracles.in_progress"), 0xFF5691FF);
    private static final HeadingWidget COMPLETED = new HeadingWidget(Component.translatable("quest.heracles.completed"), 0xFF04CB40);
    private static final HeadingWidget DEPENDENCIES = new HeadingWidget(Component.translatable("quest.heracles.dependencies"), 0xFF000080);

    private final List<MutablePair<QuestTask<?, ?, ?>, DisplayWidget>> widgets = new ArrayList<>();

    private final QuestProgress progress;
    private final String questId;
    private final ClientQuests.QuestEntry entry;
    private final Map<String, ModUtils.QuestStatus> quests;
    private final int tasksComplete;

    private final int x;
    private final int y;
    private final int width;
    private final int height;

    private double scrollAmount;
    private int lastFullHeight;

    private MouseClick mouse = null;

    private final BiConsumer<QuestTask<?, ?, ?>, Boolean> onClick;
    private final Runnable onCreate;

    public TaskListWidget(
        int x, int y, int width, int height,
        String questId, ClientQuests.QuestEntry entry, QuestProgress progress,
        Map<String, ModUtils.QuestStatus> quests, BiConsumer<QuestTask<?, ?, ?>, Boolean> onClick, Runnable onCreate
    ) {
        this.progress = progress;
        this.tasksComplete = (int) entry.value().tasks().values().stream().filter(t -> progress.getTask(t).isComplete()).count();
        this.quests = quests;
        this.questId = questId;
        this.entry = entry;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.lastFullHeight = this.height;
        this.onClick = onClick;
        this.onCreate = onCreate;
    }

    public void update(Collection<QuestTask<?, ?, ?>> tasks) {
        List<MutablePair<QuestTask<?, ?, ?>, DisplayWidget>> dependencies = new ArrayList<>();
        List<MutablePair<QuestTask<?, ?, ?>, DisplayWidget>> inProgress = new ArrayList<>();
        List<MutablePair<QuestTask<?, ?, ?>, DisplayWidget>> completed = new ArrayList<>();
        for (var task : tasks) {
            TaskProgress<?> taskProgress = this.progress.getTask(task);
            DisplayWidget widget = QuestTaskWidgets.create(this.questId, ModUtils.cast(task), taskProgress);
            if (widget != null) {
                if (taskProgress.isComplete()) {
                    completed.add(new MutablePair<>(task, widget));
                } else {
                    inProgress.add(new MutablePair<>(task, widget));
                }
            }
        }
        for (String dependency : entry.value().dependencies()) {
            ModUtils.QuestStatus status = this.quests.get(dependency);
            Quest questDependency = ClientQuests.get(dependency).map(ClientQuests.QuestEntry::value).orElse(null);
            if (status != ModUtils.QuestStatus.COMPLETED && questDependency != null) {
                dependencies.add(new MutablePair<>(null, new DependencyDisplayWidget(questDependency)));
            }
        }
        this.widgets.clear();
        this.widgets.add(new MutablePair<>(null, new TaskListHeadingWidget(this.entry.value().tasks().size(), this.tasksComplete)));
        if (!dependencies.isEmpty()) {
            this.widgets.add(new MutablePair<>(null, DEPENDENCIES));
            this.widgets.addAll(dependencies);
        }
        if (!inProgress.isEmpty()) {
            this.widgets.add(new MutablePair<>(null, IN_PROGRESS));
            this.widgets.addAll(inProgress);
        }
        if (!completed.isEmpty()) {
            this.widgets.add(new MutablePair<>(null, COMPLETED));
            this.widgets.addAll(completed);
        }
        if (this.onCreate != null) {
            this.widgets.add(new MutablePair<>(null, new AddDisplayWidget(this.onCreate)));
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x = this.x;
        int y = this.y;

        int fullHeight = 0;
        Pair<QuestTask<?, ?, ?>, Boolean> clicked = null;

        try (var scissor = RenderUtils.createScissor(Minecraft.getInstance(), graphics, x, y, width + 30, height)) {
            for (var pair : this.widgets) {
                var widget = pair.right;
                if (this.mouse != null && widget.mouseClicked(this.mouse.x() - x, this.mouse.y() - (y - this.scrollAmount), this.mouse.button(), this.width)) {
                    this.mouse = null;
                }
                int tempY = y - (int) this.scrollAmount;
                widget.render(graphics, scissor.stack(), x, tempY, this.width, mouseX, mouseY, this.isMouseOver(mouseX, mouseY), partialTick);

                var itemheight = widget.getHeight(this.width);

                boolean hovered = mouseX > x && mouseX < x + this.width + 14 && mouseY > tempY && mouseY < tempY + itemheight;

                if (hovered && pair.left != null && this.onClick != null) {
                    boolean editHovered = mouseX > x + width + 1 && mouseX < x + width + 12 && mouseY > tempY + 1 && mouseY < tempY + 12;
                    graphics.blit(AbstractQuestScreen.HEADING, x + width + 1, tempY + 1, 33, editHovered ? 26 : 15, 11, 11, 256, 256);
                    CursorUtils.setCursor(editHovered, CursorScreen.Cursor.POINTER);
                    if (mouse != null && mouse.x() > x + width + 1 && mouse.x() < x + width + 12 && mouse.y() > tempY + 1 && mouse.y() < tempY + 12 && mouse.button() == 0) {
                        clicked = Pair.of(pair.left, false);
                    }

                    boolean removeHovered = mouseX > x + width + 1 && mouseX < x + width + 12 && mouseY > tempY + 13 && mouseY < tempY + 24;
                    graphics.blit(AbstractQuestScreen.HEADING, x + width + 1, tempY + 13, 11, removeHovered ? 70 : 59, 11, 11, 256, 256);
                    CursorUtils.setCursor(removeHovered, CursorScreen.Cursor.POINTER);
                    if (mouse != null && mouse.x() > x + width + 1 && mouse.x() < x + width + 12 && mouse.y() > y + 13 && mouse.y() < y + 24 && mouse.button() == 0) {
                        clicked = Pair.of(pair.left, true);
                    }
                }

                y += itemheight;
                fullHeight += itemheight;
            }
            if (clicked != null) {
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

    public void updateTask(QuestTask<?, ?, ?> task) {
        for (var pair : this.widgets) {
            if (pair.left != null && pair.left.id().equals(task.id())) {
                var widget = QuestTaskWidgets.create(this.questId, ModUtils.cast(task), this.progress.getTask(task));
                if (widget != null) {
                    pair.left = task;
                    pair.right = widget;
                }
                break;
            }
        }
        ClientQuests.updateQuest(this.entry, quest -> {
            quest.tasks().put(task.id(), task);
            return NetworkQuestData.builder().tasks(quest.tasks());
        });
    }
}
