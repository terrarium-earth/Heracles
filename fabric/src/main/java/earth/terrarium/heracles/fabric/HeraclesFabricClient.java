package earth.terrarium.heracles.fabric;

import earth.terrarium.heracles.client.HeraclesClient;
import earth.terrarium.heracles.client.handlers.DisplayConfig;
import earth.terrarium.heracles.client.handlers.QuestTutorial;
import earth.terrarium.heracles.client.screens.pinned.PinnedQuestDisplay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.packs.PackType;

public class HeraclesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DisplayConfig.load(FabricLoader.getInstance().getGameDir());
        QuestTutorial.load(FabricLoader.getInstance().getConfigDir());
        HudRenderCallback.EVENT.register((graphics, partialTicks) -> PinnedQuestDisplay.render(graphics));
        KeyBindingHelper.registerKeyBinding(HeraclesClient.OPEN_QUESTS);
        ClientTickEvents.START_CLIENT_TICK.register(client -> HeraclesClient.clientTick());

        HeraclesClient.initReloadListeners((id, listener) ->
            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new WrappedReloadListener(id, listener))
        );
    }
}
