package earth.terrarium.heracles.client;

import earth.terrarium.heracles.client.screens.quest.QuestEditScreen;
import earth.terrarium.heracles.client.screens.quest.QuestScreen;
import earth.terrarium.heracles.client.screens.quests.QuestsEditScreen;
import earth.terrarium.heracles.client.screens.quests.QuestsScreen;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import earth.terrarium.heracles.common.menus.quests.QuestsContent;
import net.minecraft.client.Minecraft;

public class ModScreens {

    public static void openEditQuestScreen(QuestContent content) {
        Minecraft.getInstance().setScreen(new QuestEditScreen(content));
    }

    public static void openQuestScreen(QuestContent content) {
        Minecraft.getInstance().setScreen(new QuestScreen(content));
    }

    public static void openEditQuestsScreen(QuestsContent content) {
        Minecraft.getInstance().setScreen(new QuestsEditScreen(content));
    }

    public static void openQuestsScreen(QuestsContent content) {
        Minecraft.getInstance().setScreen(new QuestsScreen(content));
    }
}
