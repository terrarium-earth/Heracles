package earth.terrarium.heracles.forge.extensions;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.team.TeamProvider;
import earth.terrarium.heracles.forge.HeraclesForge;
import net.msrandom.extensions.annotations.ClassExtension;
import net.msrandom.extensions.annotations.ImplementsBaseElement;

@ClassExtension(Heracles.class)
public class HeraclesExtensions {

    @ImplementsBaseElement
    public static Iterable<TeamProvider> getTeamProviders() {
        return HeraclesForge.TEAM_PROVIDER_REGISTRY.get();
    }
}
