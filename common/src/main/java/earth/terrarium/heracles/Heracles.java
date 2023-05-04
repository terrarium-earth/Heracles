package earth.terrarium.heracles;

import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.regisitries.ModMenus;
import earth.terrarium.heracles.common.team.TeamProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.msrandom.extensions.annotations.ImplementedByExtension;
import org.apache.commons.lang3.NotImplementedException;

public class Heracles {
    public static final String MOD_ID = "heracles";

    public static final ResourceKey<Registry<TeamProvider>> TEAM_PROVIDER_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Heracles.MOD_ID, "team_provider"));

    public static void init() {
        ModMenus.MENUS.init();
        NetworkHandler.init();
    }

    @ImplementedByExtension
    public static Iterable<TeamProvider> getTeamProviders() {
        throw new NotImplementedException();
    }
}
