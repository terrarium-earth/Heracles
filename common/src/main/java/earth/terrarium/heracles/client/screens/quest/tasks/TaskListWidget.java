package earth.terrarium.heracles.client.screens.quest.tasks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.client.QuestTaskWidgets;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.screens.quest.HeadingWidget;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TaskListWidget extends AbstractContainerEventHandler implements Renderable {

    private static final Component IN_PROGRESS = Component.translatable("quest.heracles.in_progress");
    private static final Component COMPLETED = Component.translatable("quest.heracles.completed");

    private final List<DisplayWidget> widgets = new ArrayList<>();

    private final QuestProgress progress;
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

    public TaskListWidget(int x, int y, int width, int height, Quest quest, QuestProgress progress, Map<String, ModUtils.QuestStatus> quests) {
        this.progress = progress;
        this.completion = progress.isComplete() ? 1 : calculationCompletion(quest, progress);
        this.quests = quests;
        this.quest = quest;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.lastFullHeight = this.height;
    }

    @SuppressWarnings("unchecked")
    private static <F, T> T cast(F object) {
        return (T) object;
    }

    public void update(Collection<QuestTask<?, ?, ?>> tasks) {
        List<DisplayWidget> dependencies = new ArrayList<>();
        List<DisplayWidget> inProgress = new ArrayList<>();
        List<DisplayWidget> completed = new ArrayList<>();
        for (var task : tasks) {
            TaskProgress<?> taskProgress = this.progress.getTask(task);
            DisplayWidget widget = QuestTaskWidgets.create(cast(task), taskProgress);
            if (widget != null) {
                if (taskProgress.isComplete()) {
                    completed.add(widget);
                } else {
                    inProgress.add(widget);
                }
            }
        }
        for (String dependency : quest.dependencies()) {
            ModUtils.QuestStatus status = this.quests.get(dependency);
            Quest questDependency = ClientQuests.get(dependency).map(ClientQuests.QuestEntry::value).orElse(null);
            if (status != ModUtils.QuestStatus.COMPLETED && questDependency != null) {
                dependencies.add(new DependencyDisplayWidget(questDependency));
            }
        }
        this.widgets.clear();
        this.widgets.add(new TaskListHeadingWidget(this.completion));
        if (!dependencies.isEmpty()) {
            this.widgets.add(new HeadingWidget(Component.literal("Dependencies"), 0xFF000080));
            this.widgets.addAll(dependencies);
        }
        if (!inProgress.isEmpty()) {
            this.widgets.add(new HeadingWidget(IN_PROGRESS, 0xFF5691FF));
            this.widgets.addAll(inProgress);
        }
        if (!completed.isEmpty()) {
            this.widgets.add(new HeadingWidget(COMPLETED, 0xFF04CB40));
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
        try (var scissor = RenderUtils.createScissorBoxStack(new ScissorBoxStack(), Minecraft.getInstance(), pose, x, y, width, height)) {
            for (DisplayWidget widget : this.widgets) {
                if (this.mouse != null && widget.mouseClicked(this.mouse.x() - x, this.mouse.y() - (y - this.scrollAmount), this.mouse.button(), this.width)) {
                    this.mouse = null;
                }
                widget.render(pose, scissor.stack(), x, y - (int) this.scrollAmount, this.width, mouseX, mouseY, this.isMouseOver(mouseX, mouseY), partialTick);
                var itemheight = widget.getHeight(this.width);
                y += itemheight;
                fullHeight += itemheight;
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
        return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height;
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return List.of();
    }

    private record MouseClick(double x, double y, int button) {}
}
