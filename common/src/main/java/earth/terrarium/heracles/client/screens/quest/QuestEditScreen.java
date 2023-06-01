package earth.terrarium.heracles.client.screens.quest;

import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.Settings;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.QuestRewardType;
import earth.terrarium.heracles.api.rewards.QuestRewards;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.QuestTasks;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.screens.quest.rewards.RewardListWidget;
import earth.terrarium.heracles.client.screens.quest.tasks.TaskListWidget;
import earth.terrarium.heracles.client.widgets.modals.CreateObjectModal;
import earth.terrarium.heracles.client.widgets.modals.EditObjectModal;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.menus.quest.QuestMenu;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.quests.OpenQuestPacket;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class QuestEditScreen extends BaseQuestScreen {

    private TaskListWidget taskList;
    private RewardListWidget rewardList;
    private MultiLineEditBox descriptionBox;

    private CreateObjectModal createModal;

    public QuestEditScreen(QuestMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    @Override
    protected void init() {
        super.init();

        this.createModal = addTemporary(new CreateObjectModal(this.width, this.height));

        int contentX = (int) (this.width * 0.31f);
        int contentY = 30;
        int contentWidth = (int) (this.width * 0.63f);
        int contentHeight = this.height - 45;

        this.taskList = new TaskListWidget(contentX, contentY, contentWidth, contentHeight,
            this.menu.id(), this.quest(), this.menu.progress(), this.menu.quests(), (task, isRemoving) -> {
            if (isRemoving) {
                this.quest().tasks().remove(task.id());
                ClientQuests.setDirty(this.menu.id());
                ClientQuests.get(this.menu.id()).ifPresent(entry -> entry.value().tasks().remove(task.id()));
                this.taskList.update(this.quest().tasks().values());
                return;
            }
            taskPopup(ModUtils.cast(task.type()), task.id(), ModUtils.cast(task), this.taskList::updateTask);
        }, () -> {
            BiConsumer<String, QuestTaskType<?>> creator = (id, type) ->
                taskPopup(ModUtils.cast(type), id, null, newTask -> {
                    this.quest().tasks().put(id, newTask);
                    ClientQuests.setDirty(this.menu.id());
                    ClientQuests.get(this.menu.id()).ifPresent(entry -> entry.value().tasks().put(id, newTask));
                    this.taskList.update(this.quest().tasks().values());
                });

            this.createModal.setVisible(true);
            this.createModal.update(
                (type, id) -> creator.accept(id, QuestTasks.get(type)),
                (type, id) -> !this.quest().tasks().containsKey(id) && QuestTasks.types().containsKey(type),
                ConstantComponents.Tasks.CREATE,
                QuestTasks.types().values()
                    .stream()
                    .filter(questTaskType -> Settings.getFactory(questTaskType) != null)
                    .map(QuestTaskType::id)
                    .toList()
            );
        });
        this.taskList.update(this.quest().tasks().values());

        this.rewardList = new RewardListWidget(
            contentX, contentY, contentWidth, contentHeight,
            this.menu.id(), this.quest(),
            (reward, isRemoving) -> {
                if (isRemoving) {
                    this.quest().rewards().remove(reward.id());
                    ClientQuests.setDirty(this.menu.id());
                    ClientQuests.get(this.menu.id()).ifPresent(entry -> entry.value().rewards().remove(reward.id()));
                    this.rewardList.update(this.menu.id(), this.quest());
                    return;
                }
                rewardPopup(ModUtils.cast(reward.type()), reward.id(), ModUtils.cast(reward), this.rewardList::updateReward);
            }, () -> {
            BiConsumer<String, QuestRewardType<?>> creator = (id, type) ->
                rewardPopup(ModUtils.cast(type), id, null, newReward -> {
                    this.quest().rewards().put(id, newReward);
                    ClientQuests.setDirty(this.menu.id());
                    ClientQuests.get(this.menu.id()).ifPresent(entry -> entry.value().rewards().put(id, newReward));
                    this.rewardList.update(this.menu.id(), this.quest());
                });

            this.createModal.setVisible(true);
            this.createModal.update(
                (type, id) -> creator.accept(id, QuestRewards.get(type)),
                (type, id) -> !this.quest().rewards().containsKey(id) && QuestRewards.types().containsKey(type),
                ConstantComponents.Rewards.CREATE,
                QuestRewards.types().values()
                    .stream()
                    .filter(questRewardType -> Settings.getFactory(questRewardType) != null)
                    .map(QuestRewardType::id)
                    .toList()
            );
        }
        );
        this.rewardList.update(this.menu.id(), this.quest());

        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasPermissions(2)) {
            addRenderableWidget(new ImageButton(this.width - 24, 1, 11, 11, 33, 15, 11, HEADING, 256, 256, (button) ->
                NetworkHandler.CHANNEL.sendToServer(new OpenQuestPacket(this.menu.fromGroup(), this.menu.id(), false))
            )).setTooltip(Tooltip.create(ConstantComponents.TOGGLE_EDIT));
        }

        this.descriptionBox = new MultiLineEditBox(this.font, contentX, contentY, contentWidth, contentHeight, CommonComponents.EMPTY, CommonComponents.EMPTY);
        this.descriptionBox.setValue(String.join("\n", this.quest().display().description()));
    }

    private <T extends QuestTask<?, ?, T>> void taskPopup(QuestTaskType<T> type, String id, @Nullable T task, Consumer<T> consumer) {
        SettingInitializer<?> setting = Settings.getFactory(type);
        if (setting == null) return;
        EditObjectModal widget = findOrCreateEditWidget();

        SettingInitializer.CreationData data = setting.create(ModUtils.cast(task));
        widget.init(type.id(), data, savedData -> {
            var newTask = setting.create(id, ModUtils.cast(task), savedData);
            if (newTask == null) return;
            consumer.accept(ModUtils.cast(newTask));
        });
        widget.setTitle(ConstantComponents.Tasks.EDIT);
    }

    private <T extends QuestReward<T>> void rewardPopup(QuestRewardType<T> type, String id, @Nullable T reward, Consumer<T> consumer) {
        SettingInitializer<?> setting = Settings.getFactory(type);
        if (setting == null) return;
        EditObjectModal widget = findOrCreateEditWidget();
        SettingInitializer.CreationData data = setting.create(ModUtils.cast(reward));
        widget.init(type.id(), data, savedData -> {
            var newReward = setting.create(id, ModUtils.cast(reward), savedData);
            if (newReward == null) return;
            consumer.accept(ModUtils.cast(newReward));
        });
        widget.setTitle(ConstantComponents.Rewards.EDIT);
    }

    @Override
    public void removed() {
        super.removed();
        quest().display().setDescription(Arrays.asList(this.descriptionBox.getValue().split("\n")));
        ClientQuests.setDirty(this.menu.id());
        ClientQuests.sendDirty();
    }

    @Override
    public GuiEventListener getTaskList() {
        return this.taskList;
    }

    @Override
    public GuiEventListener getRewardList() {
        return this.rewardList;
    }

    @Override
    public GuiEventListener getDescriptionWidget() {
        return this.descriptionBox;
    }

    @Override
    public String getDescriptionError() {
        return null;
    }
}
