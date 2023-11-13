package earth.terrarium.heracles.forge;

import earth.terrarium.heracles.client.HeraclesClient;
import earth.terrarium.heracles.client.handlers.DisplayConfig;
import earth.terrarium.heracles.client.handlers.QuestTutorial;
import earth.terrarium.heracles.client.screens.pinned.PinnedQuestDisplay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
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

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(HeraclesForgeClient::onRegisterKeyBindings);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        DisplayConfig.load(FMLPaths.GAMEDIR.get());
        QuestTutorial.load(FMLPaths.CONFIGDIR.get());
        event.enqueueWork(HeraclesClient::init);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForgeClient::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForgeClient::onMouseClickedPreScreen);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForgeClient::onClientReloadListeners);
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

    public static void onClientReloadListeners(RegisterClientReloadListenersEvent event) {
        HeraclesClient.initReloadListeners((id, listener) -> event.registerReloadListener(listener));
    }
}
