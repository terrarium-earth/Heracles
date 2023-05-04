package earth.terrarium.heracles.client;

import earth.terrarium.heracles.api.Quest;
import earth.terrarium.heracles.client.screens.quest.QuestScreen;
import earth.terrarium.heracles.common.regisitries.ModMenus;
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

    public static void init() {
        registerScreen(ModMenus.QUEST.get(), QuestScreen::new);
    }

    public static void displayItemsRewardedToast(Quest quest, List<Item> items) {
        QuestCompletedToast.addOrUpdate(Minecraft.getInstance().getToasts(), quest, items);
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
