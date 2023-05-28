package earth.terrarium.heracles.client;

import com.mojang.blaze3d.platform.InputConstants;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.handlers.DisplayConfig;
import earth.terrarium.heracles.client.handlers.QuestTutorial;
import earth.terrarium.heracles.client.screens.QuestTutorialScreen;
import earth.terrarium.heracles.client.screens.quest.QuestEditScreen;
import earth.terrarium.heracles.client.screens.quest.QuestScreen;
import earth.terrarium.heracles.client.screens.quests.QuestsEditScreen;
import earth.terrarium.heracles.client.screens.quests.QuestsScreen;
import earth.terrarium.heracles.client.toasts.QuestClaimedToast;
import earth.terrarium.heracles.client.toasts.QuestCompletedToast;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.groups.OpenGroupPacket;
import earth.terrarium.heracles.common.regisitries.ModMenus;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Objects;

public class HeraclesClient {

    public static final KeyMapping OPEN_QUESTS = new KeyMapping(
        "key.heracles.open_quests",
        InputConstants.KEY_U,
        "key.categories.odyssey"
    );

    public static void init() {
        Heracles.setRegistryAccess(() -> Objects.requireNonNull(Minecraft.getInstance().getConnection()).registryAccess());
    }

    public static void onScreenConstruction(ScreenConstructionEvent event) {
        event.registerScreen(ModMenus.QUEST.get(), QuestScreen::new);
        event.registerScreen(ModMenus.EDIT_QUEST.get(), QuestEditScreen::new);
        event.registerScreen(ModMenus.QUESTS.get(), QuestsScreen::new);
        event.registerScreen(ModMenus.EDIT_QUESTS.get(), QuestsEditScreen::new);
    }

    public static void clientTick() {
        if (OPEN_QUESTS.consumeClick()) {
            if (DisplayConfig.showTutorial) {
                if (!QuestTutorial.tutorialText().isBlank()) {
                    Minecraft.getInstance().setScreen(new QuestTutorialScreen());
                } else {
                    DisplayConfig.showTutorial = false;
                    DisplayConfig.save();
                    NetworkHandler.CHANNEL.sendToServer(new OpenGroupPacket("", false));
                }
            } else {
                NetworkHandler.CHANNEL.sendToServer(new OpenGroupPacket("", false));
            }
        }
        QuestTutorial.tick();
    }

    public static void displayItemsRewardedToast(Quest quest, List<Item> items) {
        QuestClaimedToast.addOrUpdate(Minecraft.getInstance().getToasts(), quest, items);
    }

    public static void displayQuestCompleteToast(Quest quest) {
        QuestCompletedToast.add(Minecraft.getInstance().getToasts(), quest);
    }

    public interface ScreenConstructionEvent {

        <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerScreen(MenuType<? extends M> type, ScreenConstructor<M, U> factory);
    }

    public interface ScreenConstructor<T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> {
        U create(T menu, Inventory inventory, Component component);
    }
}
