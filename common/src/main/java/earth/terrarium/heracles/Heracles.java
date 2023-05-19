package earth.terrarium.heracles;

import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.regisitries.ModItems;
import earth.terrarium.heracles.common.regisitries.ModMenus;
import earth.terrarium.heracles.common.team.TeamProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.msrandom.extensions.annotations.ImplementedByExtension;

import java.util.List;
import java.util.function.Supplier;

public class Heracles {
    public static final String MOD_ID = "heracles";

    public static final ResourceKey<Registry<TeamProvider>> TEAM_PROVIDER_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Heracles.MOD_ID, "team_provider"));

    private static Supplier<RegistryAccess> registryAccessSupplier;

    public static void init() {
        ModMenus.MENUS.init();
        ModItems.ITEMS.init();
        NetworkHandler.init();
    }

    @ImplementedByExtension
    public static Iterable<TeamProvider> getTeamProviders() {
        System.out.println("getTeamProviders not implemented");
        //throw new NotImplementedException();
        return List.of();
    }

    public static void setRegistryAccess(Supplier<RegistryAccess> access) {
        Heracles.registryAccessSupplier = access;
    }

    public static RegistryAccess getRegistryAccess() {
        return Heracles.registryAccessSupplier.get();
    }
}
