package earth.terrarium.olympus.client.neoforge;

import earth.terrarium.olympus.client.images.ImageProviders;
import earth.terrarium.olympus.client.pipelines.pips.RoundedRectanglePIPRenderer;
import earth.terrarium.olympus.client.pipelines.pips.RoundedTexturePIPRenderer;
import earth.terrarium.olympus.client.ui.UIConstants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = UIConstants.MOD_ID, dist = Dist.CLIENT)
public class OlympusNeoForgeClient {

    public OlympusNeoForgeClient(IEventBus bus) {
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Pre ignored) -> ImageProviders.tick());
        bus.addListener((RegisterPictureInPictureRenderersEvent event) -> {
            event.register(RoundedRectanglePIPRenderer.State.class, RoundedRectanglePIPRenderer::new);
            event.register(RoundedTexturePIPRenderer.State.class, RoundedTexturePIPRenderer::new);
        });
    }
}
