package earth.terrarium.heracles.client.components.lists.tasks.entries;

import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.client.QuestTaskWidgets;
import earth.terrarium.heracles.client.components.lists.AbstractEditListEntry;
import earth.terrarium.heracles.client.components.lists.QuestList;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.ui.modals.EditObjectModal;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import earth.terrarium.heracles.common.network.packets.quests.data.NetworkQuestData;
import earth.terrarium.heracles.common.utils.ModUtils;

public final class EditQuestTaskEntry extends AbstractEditListEntry<QuestTask<?, ?, ?>> {

    public EditQuestTaskEntry(QuestList<QuestTask<?, ?, ?>> list, QuestTask<?, ?, ?> task, DisplayWidget widget) {
        super(list, task, widget);
    }

    @Override
    protected void setValue(QuestTask<?, ?, ?> task) {
        QuestContent content = this.list.content();
        TaskProgress<?> progress = content.progress().getTask(task);
        DisplayWidget newWidget = QuestTaskWidgets.create(content.id(), ModUtils.cast(task), progress, content.quests().get(content.id()));
        if (newWidget != null) {
            this.value = task;
            this.widget = newWidget;
        }
        ClientQuests.updateQuest(entry(), quest -> {
            quest.tasks().put(task.id(), task);
            return NetworkQuestData.builder().tasks(quest.tasks());
        });
    }

    @Override
    protected void edit() {
        EditObjectModal.open(ModUtils.cast(this.value.type()), ConstantComponents.Tasks.EDIT, this.id(), this.value, this::setValue);
    }

    @Override
    protected void delete() {
        ClientQuests.updateQuest(entry(), quest -> {
            quest.tasks().remove(id());
            return NetworkQuestData.builder().tasks(quest.tasks());
        });
        this.list.update();
    }

    @Override
    public String id() {
        return this.value.id();
    }
}
