package earth.terrarium.heracles.fabric.extensions;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.fabric.HeraclesFabric;
import earth.terrarium.heracles.common.team.TeamProvider;
import net.msrandom.extensions.annotations.ClassExtension;
import net.msrandom.extensions.annotations.ImplementsBaseElement;

@ClassExtension(Heracles.class)
public class HeraclesExtensions {
    @ImplementsBaseElement
    public static Iterable<TeamProvider> getTeamProviders() {
        return HeraclesFabric.TEAM_PROVIDER_REGISTRY;
    }
}
