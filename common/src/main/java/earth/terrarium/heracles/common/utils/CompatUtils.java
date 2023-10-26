package earth.terrarium.heracles.common.utils;

import com.teamresourceful.resourcefullib.common.utils.modinfo.ModInfo;
import com.teamresourceful.resourcefullib.common.utils.modinfo.ModInfoUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class CompatUtils {
    public static String guessModTitle(String namespace) {
        Optional<ModInfo> info = Optional.ofNullable(ModInfoUtils.getModInfo(namespace))
            .or(() -> Optional.ofNullable(ModInfoUtils.getModInfo(namespace.replace("_", "-"))));
        if (info.isPresent()) {
            return info.get().displayName();
        }
        String prettifiedNamespace = StringUtils.capitalize(namespace.replace("_", " ").replace("/", " 🡲 "));
        if (prettifiedNamespace.equals("C")) prettifiedNamespace = "Common";
        return prettifiedNamespace;
    }
}
