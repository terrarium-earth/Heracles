package earth.terrarium.heracles;

import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.regisitries.ModItems;
import net.minecraft.core.RegistryAccess;

import java.util.function.Supplier;

public class Heracles {
    public static final String MOD_ID = "heracles";

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
}
