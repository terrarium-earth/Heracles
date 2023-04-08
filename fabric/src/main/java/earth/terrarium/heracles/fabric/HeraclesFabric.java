package earth.terrarium.heracles.fabric;

import earth.terrarium.heracles.Heracles;
import net.fabricmc.api.ModInitializer;

public class HeraclesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Heracles.init();
    }
}
