package earth.terrarium.heracles.common.utils;

import com.teamresourceful.resourcefullib.common.utils.modinfo.ModInfo;
import com.teamresourceful.resourcefullib.common.utils.modinfo.ModInfoUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Optional;

public class CompatUtils {
    @SuppressWarnings("deprecation")
    public static String guessModTitle(String namespace) {
        Optional<ModInfo> info = Optional.ofNullable(ModInfoUtils.getModInfo(namespace))
            .or(() -> Optional.ofNullable(ModInfoUtils.getModInfo(namespace.replace("_", "-"))));
        if (info.isPresent()) {
            return info.get().displayName();
        }
        String prettifiedNamespace = WordUtils.capitalizeFully(namespace.replace("_", " ").replace("/", " 🡲 "));
        if (prettifiedNamespace.equals("C")) prettifiedNamespace = "Common";
        return prettifiedNamespace;
    }
}
