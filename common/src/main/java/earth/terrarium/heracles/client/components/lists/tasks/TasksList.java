package earth.terrarium.heracles.client.components.lists.tasks;

import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.client.QuestTaskWidgets;
import earth.terrarium.heracles.client.ui.UIConstants;
import earth.terrarium.heracles.client.components.lists.HeadingListEntry;
import earth.terrarium.heracles.client.components.lists.ListEntry;
import earth.terrarium.heracles.client.components.lists.QuestList;
import earth.terrarium.heracles.client.components.lists.QuestValueEntry;
import earth.terrarium.heracles.client.components.lists.tasks.entries.DependencyTaskEntry;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TasksList extends QuestList<QuestTask<?, ?, ?>> {

    private static final ListEntry<QuestTask<?, ?, ?>> IN_PROGRESS = new HeadingListEntry<>(Component.translatable("quest.heracles.in_progress"), UIConstants.IN_PROGRESS_HEADING);
    private static final ListEntry<QuestTask<?, ?, ?>> COMPLETED = new HeadingListEntry<>(Component.translatable("quest.heracles.completed"), UIConstants.CLAIMED_HEADING);
    private static final ListEntry<QuestTask<?, ?, ?>> DEPENDENCIES = new HeadingListEntry<>(Component.translatable("quest.heracles.dependencies"), UIConstants.DEPENDENTS_HEADING);

    public TasksList(@Nullable QuestList<QuestTask<?, ?, ?>> list, int width, int height, QuestContent content) {
        super(list, width, height, content);
    }

    public TasksList(int width, int height, QuestContent content) {
        super(width, height, content);
    }

    @Override
    public ListEntry<QuestTask<?, ?, ?>> create(QuestTask<?, ?, ?> task, DisplayWidget widget) {
        return new QuestValueEntry<>(task, widget);
    }

    @Override
    public void update(String group) {
        ClientQuests.QuestEntry entry = ClientQuests.get(this.content().id()).orElse(null);
        if (entry == null) return;

        List<ListEntry<QuestTask<?, ?, ?>>> dependencies = new ArrayList<>();
        List<ListEntry<QuestTask<?, ?, ?>>> inProgress = new ArrayList<>();
        List<ListEntry<QuestTask<?, ?, ?>>> completed = new ArrayList<>();

        QuestContent content = this.content();

        for (var task : entry.value().tasks().values()) {
            TaskProgress<?> progress = content.progress().getTask(task);
            DisplayWidget widget = QuestTaskWidgets.create(content.id(), ModUtils.cast(task), progress, content.quests().get(content.id()));
            if (widget == null) continue;
            ListEntry<QuestTask<?, ?, ?>> taskEntry = create(task, widget);
            if (progress.isComplete()) {
                completed.add(taskEntry);
            } else {
                inProgress.add(taskEntry);
            }
        }

        for (ClientQuests.QuestEntry child : entry.dependencies()) {
            ModUtils.QuestStatus status = content.quests().get(child.key());
            if (status.isComplete()) continue;
            if (child.value() == null) continue;
            dependencies.add(new DependencyTaskEntry(child.value()));
        }

        this.clear();
        if (!dependencies.isEmpty()) {
            this.add(DEPENDENCIES);
            dependencies.forEach(this::add);
        }
        if (!inProgress.isEmpty()) {
            this.add(IN_PROGRESS);
            inProgress.forEach(this::add);
        }
        if (!completed.isEmpty()) {
            this.add(COMPLETED);
            completed.forEach(this::add);
        }
    }
}
