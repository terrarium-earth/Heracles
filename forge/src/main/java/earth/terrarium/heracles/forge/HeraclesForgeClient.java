package earth.terrarium.heracles.forge;

import earth.terrarium.heracles.client.HeraclesClient;
import earth.terrarium.heracles.client.handlers.DisplayConfig;
import earth.terrarium.heracles.client.handlers.QuestTutorial;
import earth.terrarium.heracles.client.screens.pinned.PinnedQuestDisplay;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class HeraclesForgeClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        DisplayConfig.load(FMLPaths.GAMEDIR.get());
        QuestTutorial.load(FMLPaths.CONFIGDIR.get());
        event.enqueueWork(HeraclesClient::init);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForgeClient::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForgeClient::onMouseClickedPreScreen);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(HeraclesForgeClient::onRegisterKeyBindings);
        event.enqueueWork(() ->
            HeraclesClient.onScreenConstruction(new HeraclesClient.ScreenConstructionEvent() {
                @Override
                public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerScreen(MenuType<? extends M> type, HeraclesClient.ScreenConstructor<M, U> factory) {
                    MenuScreens.register(type, factory::create);
                }
            })
        );
    }

    public static void onRegisterKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(HeraclesClient.OPEN_QUESTS);
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.START)) {
            HeraclesClient.clientTick();
        }
    }

    public static void onMouseClickedPreScreen(ScreenEvent.MouseButtonPressed.Pre event) {
        if (PinnedQuestDisplay.click(event.getMouseX(), event.getMouseY())) {
            event.setCanceled(true);
        }
    }
}
