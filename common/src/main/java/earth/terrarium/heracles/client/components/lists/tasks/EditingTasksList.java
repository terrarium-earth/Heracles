package earth.terrarium.heracles.client.components.lists.tasks;

import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.client.components.lists.ListEntry;
import earth.terrarium.heracles.client.components.lists.QuestList;
import earth.terrarium.heracles.client.components.lists.tasks.entries.EditQuestTaskEntry;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.quests.ServerboundResetProgressPacket;
import org.jetbrains.annotations.Nullable;

public class EditingTasksList extends TasksList {

    public EditingTasksList(@Nullable QuestList<QuestTask<?, ?, ?>> list, int width, int height, QuestContent content) {
        super(list, width, height, content);
    }

    @Override
    public ListEntry<QuestTask<?, ?, ?>> create(QuestTask<?, ?, ?> task, DisplayWidget widget) {
        return new EditQuestTaskEntry(this, task, widget);
    }

    @Override
    public void resetProgress(QuestTask<?, ?, ?> task) {
        TaskProgress<?> progress = this.content().progress().getTask(task);
        progress.reset();
        this.content().progress().setComplete(false);
        NetworkHandler.CHANNEL.sendToServer(new ServerboundResetProgressPacket(this.content().id(), task.id(), true));
        update();
    }
}
