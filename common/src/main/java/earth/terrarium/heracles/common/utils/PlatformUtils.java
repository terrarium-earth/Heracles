package earth.terrarium.heracles.common.utils;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class PlatformUtils {
    @ExpectPlatform
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
    ```
    This would also mean that you can remove the dependency on arch expect platform again.
        throw new AssertionError("Not implemented");
    }
}
