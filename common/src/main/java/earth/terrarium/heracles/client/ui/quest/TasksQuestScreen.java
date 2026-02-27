package earth.terrarium.heracles.client.ui.quest;

import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.client.components.lists.QuestList;
import earth.terrarium.heracles.client.components.lists.tasks.TasksList;
import earth.terrarium.heracles.client.ui.QuestTab;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.Screen;

import java.util.concurrent.atomic.AtomicInteger;

public class TasksQuestScreen extends AbstractQuestScreen {

    private QuestList<QuestTask<?, ?, ?>> list = null;

    public TasksQuestScreen(Screen parent, QuestContent content) {
        super(parent, content, QuestTab.TASKS);
    }

    @Override
    protected GridLayout initContent(AtomicInteger row) {
        GridLayout layout = super.initContent(row);
        this.list = layout.addChild(
            new TasksList(this.list, this.contentWidth - 40, this.contentHeight, this.content),
            row.getAndIncrement(), 0,
            LayoutSettings.defaults().paddingHorizontal(20).paddingVertical(5)
        );
        return layout;
    }
}
