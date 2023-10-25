package earth.terrarium.heracles.common.utils.forge;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class PlatformUtilsImpl {
    public static String guessModTitle(String namespace) {
        @SuppressWarnings("unchecked")
        Optional<ModContainer> mod = ((Optional<ModContainer>) ModList.get().getModContainerById(namespace)).or(() -> ModList.get().getModContainerById(namespace.replace('_', '-')));
        if (mod.isPresent()) {
            return mod.get().getModInfo().getDisplayName();
        }
        String prettifiedNamespace = StringUtils.capitalize(namespace.replace("_", " ").replace("/", " 🡲 "));
        if (prettifiedNamespace.equals("C")) prettifiedNamespace = "Common";
        return prettifiedNamespace;
    }
}
