package earth.terrarium.heracles;

import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.regisitries.ModItems;
import net.minecraft.core.RegistryAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.function.Supplier;

public class Heracles {
    public static final String MOD_ID = "heracles";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static Path configPath;
    private static Supplier<RegistryAccess> registryAccessSupplier;

    public static void init() {
        ModItems.ITEMS.init();
        NetworkHandler.init();
    }

    public static void setRegistryAccess(Supplier<RegistryAccess> access) {
        Heracles.registryAccessSupplier = access;
    }

    public static RegistryAccess getRegistryAccess() {
        return Heracles.registryAccessSupplier.get();
    }

    public static void setConfigPath(Path path) {
        Heracles.configPath = path;
    }

    public static Path getConfigPath() {
        return Heracles.configPath;
    }
}
