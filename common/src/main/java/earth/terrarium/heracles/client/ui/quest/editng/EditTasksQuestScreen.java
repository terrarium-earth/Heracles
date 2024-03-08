package earth.terrarium.heracles.client.ui.quest.editng;

import earth.terrarium.heracles.api.client.settings.Settings;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.QuestTasks;
import earth.terrarium.heracles.client.components.lists.QuestList;
import earth.terrarium.heracles.client.components.lists.tasks.EditingTasksList;
import earth.terrarium.heracles.client.components.widgets.buttons.TextButton;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.ui.quest.AbstractQuestScreen;
import earth.terrarium.heracles.client.ui.QuestTab;
import earth.terrarium.heracles.client.ui.modals.CreateObjectModal;
import earth.terrarium.heracles.client.ui.modals.EditObjectModal;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import earth.terrarium.heracles.common.network.packets.quests.data.NetworkQuestData;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EditTasksQuestScreen extends AbstractQuestScreen {

    private QuestList<QuestTask<?, ?, ?>> list = null;

    public EditTasksQuestScreen(Screen parent, QuestContent content) {
        super(parent, content, QuestTab.TASKS);
    }

    @Override
    protected GridLayout initSidebar(AtomicInteger row) {
        addRenderableWidget(TextButton.create(
            this.sideBarWidth - SPACER - PADDING * 2, BUTTON_HEIGHT,
            Component.literal("Add Task"),
            () -> CreateObjectModal.open("tasks", this::createTask, this::isValidCreation, getValidTypes())
        )).setPosition(PADDING, this.height - BUTTON_HEIGHT - PADDING);

        return super.initSidebar(row);
    }

    @Override
    protected GridLayout initContent(AtomicInteger row) {
        GridLayout layout = super.initContent(row);
        this.list = layout.addChild(
            new EditingTasksList(this.list, this.contentWidth - 40, this.contentHeight, this.content),
            row.getAndIncrement(), 0,
            LayoutSettings.defaults().paddingHorizontal(20).paddingVertical(5)
        );
        return layout;
    }

    private void createTask(ResourceLocation type, String id) {
        EditObjectModal.open(QuestTasks.get(type), ConstantComponents.Tasks.EDIT, id, null, task -> {
            ClientQuests.get(this.content().id()).ifPresent(entry -> ClientQuests.updateQuest(entry, quest -> {
                quest.tasks().put(id, task);
                return NetworkQuestData.builder().tasks(quest.tasks());
            }));
            this.list.update(this.content().fromGroup());
        });
    }

    private boolean isValidCreation(ResourceLocation type, String id) {
        return !this.quest().tasks().containsKey(id) && type != null && QuestTasks.types().containsKey(type);
    }

    private List<ResourceLocation> getValidTypes() {
        return QuestTasks.types().values()
            .stream()
            .filter(type -> Settings.getFactory(type) != null)
            .map(QuestTaskType::id)
            .toList();
    }
}
