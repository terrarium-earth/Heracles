package earth.terrarium.heracles.client.ui;

import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.ui.quest.AbstractQuestScreen;
import earth.terrarium.heracles.client.ui.quest.DescriptionQuestScreen;
import earth.terrarium.heracles.client.ui.quest.RewardsQuestScreen;
import earth.terrarium.heracles.client.ui.quest.TasksQuestScreen;
import earth.terrarium.heracles.client.ui.quest.editng.EditDescriptionQuestScreen;
import earth.terrarium.heracles.client.ui.quest.editng.EditRewardsQuestScreen;
import earth.terrarium.heracles.client.ui.quest.editng.EditTasksQuestScreen;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public enum QuestTab {
    OVERVIEW,
    TASKS,
    REWARDS;

    private static boolean editing = false;

    public static boolean toggleEditing() {
        if (!canEdit()) return false;
        QuestTab.editing = !QuestTab.editing;
        return true;
    }

    public static boolean isEditing() {
        if (!canEdit()) {
            QuestTab.editing = false;
        }
        return QuestTab.editing;
    }

    public static boolean canEdit() {
        return Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasPermissions(Commands.LEVEL_GAMEMASTERS);
    }

    public Component getTitle() {
        return switch (this) {
            case OVERVIEW -> ConstantComponents.Quests.OVERVIEW;
            case TASKS -> ConstantComponents.Tasks.TITLE;
            case REWARDS -> ConstantComponents.Rewards.TITLE;
        };
    }

    public boolean canBeShown(QuestContent content) {
        if (QuestTab.editing) return true;
        Quest quest = ClientQuests.get(content.id()).map(ClientQuests.QuestEntry::value).orElse(null);
        if (quest == null) return false;
        boolean canShowTasks = !quest.tasks().isEmpty();
        boolean canShowRewards = !quest.rewards().isEmpty();
        return switch (this) {
            case OVERVIEW -> canShowTasks || canShowRewards;
            case TASKS -> canShowTasks;
            case REWARDS -> canShowRewards;
        };
    }

    public void open(QuestContent content) {
        Screen parent = Minecraft.getInstance().screen;
        if (parent instanceof AbstractQuestScreen screen) {
            parent = screen.parent();
        }
        Screen screen;
        if (QuestTab.isEditing()) {
            screen = switch (this) {
                case TASKS -> new EditTasksQuestScreen(parent, content);
                case REWARDS -> new EditRewardsQuestScreen(parent, content);
                case OVERVIEW -> new EditDescriptionQuestScreen(parent, content);
            };
        } else {
            screen = switch (this) {
                case TASKS -> new TasksQuestScreen(parent, content);
                case REWARDS -> new RewardsQuestScreen(parent, content);
                case OVERVIEW -> new DescriptionQuestScreen(parent, content);
            };
        }
        Minecraft.getInstance().setScreen(screen);
    }
}
