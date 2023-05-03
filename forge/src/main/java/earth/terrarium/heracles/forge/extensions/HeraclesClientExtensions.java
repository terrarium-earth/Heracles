package earth.terrarium.heracles.forge.extensions;

import earth.terrarium.heracles.client.HeraclesClient;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.msrandom.extensions.annotations.ClassExtension;
import net.msrandom.extensions.annotations.ImplementsBaseElement;

@ClassExtension(HeraclesClient.class)
public class HeraclesClientExtensions {
    @ImplementsBaseElement
    public static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerScreen(MenuType<? extends M> type, HeraclesClient.ScreenConstructor<M, U> factory) {
        MenuScreens.register(type, factory::create);
    }
}