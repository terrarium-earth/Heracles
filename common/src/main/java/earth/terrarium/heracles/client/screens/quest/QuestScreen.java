package earth.terrarium.heracles.client.screens.quest;

import earth.terrarium.heracles.client.screens.quest.tasks.TaskListWidget;
import earth.terrarium.heracles.common.menus.quest.QuestMenu;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class QuestScreen extends BaseQuestScreen {

    private TaskListWidget taskList;

    public QuestScreen(QuestMenu menu, Inventory inventory, Component component) {
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
    }

    @Override
    public GuiEventListener getTaskList() {
        return this.taskList;
    }
}
