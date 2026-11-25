package earth.terrarium.heracles;

import com.mojang.logging.LogUtils;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.regisitries.ModBlocks;
import earth.terrarium.heracles.common.regisitries.ModItems;
import earth.terrarium.heracles.common.regisitries.ModLootConditions;
import earth.terrarium.heracles.common.regisitries.ModSounds;import earth.terrarium.heracles.common.utils.PlatformLogger;
import earth.terrarium.heracles.common.utils.PlatformSettings;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.function.Supplier;

public class Heracles {
    public static final String MOD_ID = "heracles";
    public static final PlatformLogger LOGGER = PlatformLogger.of(LogUtils.getLogger());

    private static Path configPath;
    private static Supplier<RegistryAccess> registryAccessSupplier;

    public static void init(PlatformSettings settings) {
        settings.apply(Heracles.LOGGER);
        ModBlocks.BLOCKS.init();
        ModBlocks.BLOCK_ENTITIES.init();
        ModItems.ITEMS.init();
        ModLootConditions.TYPES.init();
        ModSounds.SOUNDS.init();
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

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
