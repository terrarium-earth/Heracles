package earth.terrarium.heracles.forge;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.HeraclesClient;
import earth.terrarium.heracles.client.handlers.DisplayConfig;
import earth.terrarium.heracles.client.handlers.QuestTutorial;
import earth.terrarium.heracles.client.screens.pinned.PinnedQuestDisplay;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

@EventBusSubscriber(modid = Heracles.MOD_ID, value = Dist.CLIENT)
public class HeraclesNeoForgeClient {

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(HeraclesNeoForgeClient::onClientSetup);
        modEventBus.addListener(HeraclesNeoForgeClient::onRegisterKeyBindings);
        modEventBus.addListener(HeraclesNeoForgeClient::onClientReloadListeners);
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            DisplayConfig.load(FMLPaths.GAMEDIR.get());
            QuestTutorial.load(FMLPaths.CONFIGDIR.get());
        });

        NeoForge.EVENT_BUS.addListener(HeraclesNeoForgeClient::onClientTick);
    }

    public static void onRegisterKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(HeraclesClient.OPEN_QUESTS);
    }

    public static void onClientTick(ClientTickEvent.Pre event) {
            HeraclesClient.clientTick();
    }

    @SubscribeEvent
    public static void onMouseClickedPreScreen(ScreenEvent.MouseButtonPressed.Pre event) {
        if (PinnedQuestDisplay.click(event.getMouseX(), event.getMouseY())) {
            event.setCanceled(true);
        }
    }

    public static void onClientReloadListeners(RegisterClientReloadListenersEvent event) {
        HeraclesClient.initReloadListeners((id, listener) -> event.registerReloadListener(listener));
    }
}
