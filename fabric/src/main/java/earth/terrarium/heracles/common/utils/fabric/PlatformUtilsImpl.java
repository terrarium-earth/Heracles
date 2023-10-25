package earth.terrarium.heracles.common.utils.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class PlatformUtilsImpl {
    public static String guessModTitle(String namespace) {
        Optional<ModContainer> mod = FabricLoader.getInstance().getModContainer(namespace).or(() -> FabricLoader.getInstance().getModContainer(namespace.replace('_', '-')));
        if (mod.isPresent()) {
            return mod.get().getMetadata().getName();
        }
        String prettifiedNamespace = StringUtils.capitalize(namespace.replace("_", " ").replace("/", " 🡲 "));
        if (prettifiedNamespace.equals("C")) prettifiedNamespace = "Common";
        return prettifiedNamespace;
    }
}
