package earth.terrarium.heracles;

import com.mojang.logging.LogUtils;
import earth.terrarium.heracles.api.events.HeraclesEvents;
import earth.terrarium.heracles.api.events.QuestEventTarget;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.regisitries.ModItems;
import earth.terrarium.heracles.common.regisitries.ModLootConditions;
import earth.terrarium.heracles.common.utils.PlatformLogger;
import earth.terrarium.heracles.common.utils.PlatformSettings;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.nio.file.Path;
import java.util.function.Supplier;

public class Heracles {
    public static final String MOD_ID = "heracles";
    public static final PlatformLogger LOGGER = PlatformLogger.of(LogUtils.getLogger());

    private static Path configPath;
    private static Supplier<RegistryAccess> registryAccessSupplier;

    public static void init(PlatformSettings settings) {
        settings.apply(Heracles.LOGGER);
        ModItems.ITEMS.init();
        ModLootConditions.TYPES.init();
        NetworkHandler.init();
        HeraclesEvents.QuestCompleteListener.register(Heracles::playQuestCompleteSound);
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

    private static void playQuestCompleteSound(QuestEventTarget event) {
        ServerPlayer player = event.player();
        player.level().playSound(null, player.blockPosition(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.MASTER, 0.1f, 2f);
    }
}
