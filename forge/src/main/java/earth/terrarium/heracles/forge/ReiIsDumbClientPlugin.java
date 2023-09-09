package earth.terrarium.heracles.forge;

import earth.terrarium.heracles.client.compat.rei.HeraclesReiClientPlugin;
import me.shedaniel.rei.forge.REIPluginClient;

/**
 * This class is required to be present in order for the REI plugin to be loaded.
 * It doesn't do anything, but it's required.
 * <p>
 * Rei is dumb that it does not include this in common as there is literally no reason for it to not be this just makes
 * it so that you need to have a dummy class in the forge package to make it work.
 */
@REIPluginClient
public class ReiIsDumbClientPlugin extends HeraclesReiClientPlugin {

}
