package earth.terrarium.heracles.fabric;

import earth.terrarium.heracles.client.handlers.DisplayConfig;
import earth.terrarium.heracles.client.screens.pinned.PinnedQuestDisplay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;

public class HeraclesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DisplayConfig.load(FabricLoader.getInstance().getConfigDir());
        HudRenderCallback.EVENT.register((pose, partialTicks) -> PinnedQuestDisplay.render(pose));
    }
}
