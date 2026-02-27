package earth.terrarium.heracles.client;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.screens.quests.QuestsEditScreen;
import earth.terrarium.heracles.client.ui.QuestTab;
import earth.terrarium.heracles.client.ui.quests.AbstractQuestsScreen;
import earth.terrarium.heracles.client.ui.quests.EditQuestsScreen;
import earth.terrarium.heracles.client.ui.quests.QuestsScreen;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import earth.terrarium.heracles.common.menus.quests.QuestsContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class ModScreens {

    public static void openQuest(QuestContent content) {
        QuestTab.OVERVIEW.open(content);
    }

    public static void openQuests(QuestsContent content) {
        boolean isEditing = QuestTab.isEditing();
        Screen parent = Minecraft.getInstance().screen;
        Screen screen;
        if (isEditing) {
            screen = new EditQuestsScreen(parent, content);
        } else {
            screen = new QuestsScreen(parent, content);
        }
        Minecraft.getInstance().setScreen(screen);
    }

    public static void openEditQuestsScreen(QuestsContent content) {
        var screen = Minecraft.getInstance().screen;
        Heracles.LOGGER.debug("Opening edit quests screen for {}", content);
        Minecraft.getInstance().setScreen(new EditQuestsScreen(screen, content));
    }

    public static void openQuestsScreen(QuestsContent content) {
        var screen = Minecraft.getInstance().screen;
        Heracles.LOGGER.debug("Opening quests screen for {}", content);
        Minecraft.getInstance().setScreen(new QuestsScreen(screen, content));
//        Minecraft.getInstance().setScreen(new QuestsScreen(content));
    }
}
