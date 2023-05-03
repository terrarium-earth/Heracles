package earth.terrarium.heracles.forge;

import earth.terrarium.heracles.client.HeraclesClient;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class HeraclesForgeClient {

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(HeraclesForgeClient::onClientSetup);
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(HeraclesClient::init);
    }
}
