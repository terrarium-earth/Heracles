package earth.terrarium.heracles.client.screens.quest;

import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.Settings;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.screens.quest.tasks.TaskListWidget;
import earth.terrarium.heracles.client.widgets.base.TemporyWidget;
import earth.terrarium.heracles.client.widgets.modals.EditObjectModal;
import earth.terrarium.heracles.common.menus.quest.QuestMenu;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class QuestEditScreen extends BaseQuestScreen {

    private TaskListWidget taskList;

    public QuestEditScreen(QuestMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    @Override
    protected void init() {
        super.init();

        int contentX = (int) (this.width * 0.31f);
        int contentY = 30;
        int contentWidth = (int) (this.width * 0.63f);
        int contentHeight = this.height - 45;

        this.taskList = new TaskListWidget(contentX, contentY, contentWidth, contentHeight, this.menu.id(), this.menu.quest(), this.menu.progress(), this.menu.quests());
        this.taskList.update(this.menu.quest().tasks().values());
        this.taskList.setOnClick((task, isRemoving) -> {
            if (isRemoving) {
                this.taskList.removeTask(task);
                return;
            }
            SettingInitializer<?> setting = Settings.getFactory(task.type());
            if (setting == null) return;

            boolean found = false;
            EditObjectModal widget = new EditObjectModal(this.width, this.height);
            for (TemporyWidget temporaryWidget : this.temporaryWidgets()) {
                if (temporaryWidget instanceof EditObjectModal modal) {
                    found = true;
                    widget = modal;
                    break;
                }
            }
            widget.setVisible(true);

            SettingInitializer.CreationData data = setting.create(ModUtils.cast(task));
            widget.init(task.type().id(), data, savedData -> {
                var newTask = setting.create(task.id(), ModUtils.cast(task), savedData);
                if (newTask == null) return;
                this.taskList.updateTask(ModUtils.cast(newTask));
            });
            if (!found) {
                this.addTemporary(widget);
            }
        });
    }

    @Override
    public void removed() {
        super.removed();
        ClientQuests.sendDirty();
    }

    @Override
    public GuiEventListener getTaskList() {
        return this.taskList;
    }
}
