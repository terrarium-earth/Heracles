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
import earth.terrarium.heracles.common.network.packets.OpenGroupPacket;
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
import net.msrandom.extensions.annotations.ImplementedByExtension;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;

public class HeraclesClient {

    public static final KeyMapping OPEN_QUESTS = new KeyMapping(
        "key.heracles.open_quests",
        InputConstants.KEY_U,
        "key.categories.odyssey"
    );

    public static void init() {
        registerScreen(ModMenus.QUEST.get(), QuestScreen::new);
        registerScreen(ModMenus.EDIT_QUEST.get(), QuestEditScreen::new);
        registerScreen(ModMenus.QUESTS.get(), QuestsScreen::new);
        registerScreen(ModMenus.EDIT_QUESTS.get(), QuestsEditScreen::new);

        Heracles.setRegistryAccess(() -> Minecraft.getInstance().getConnection().registryAccess());
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

    @ImplementedByExtension
    public static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerScreen(MenuType<? extends M> type, ScreenConstructor<M, U> factory) {
        throw new NotImplementedException();
    }

    //TODO Annotate for client
    public interface ScreenConstructor<T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> {
        U create(T menu, Inventory inventory, Component component);
    }
}
