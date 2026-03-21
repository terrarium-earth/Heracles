package earth.terrarium.olympus.client.utils;

import com.teamresourceful.resourcefullib.common.color.Color;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class OlympusUtils {

    public static int getEnsureAlpha(Color color) {
        int value = color.getValue();
        int alpha = value >> 24 & 255;
        return alpha == 0 ? value | 0xff000000 : value;
    }

    public static String removePrefix(String text, String... prefixes) {
        for (String prefix : prefixes) {
            if (text.startsWith(prefix)) {
                text = text.substring(prefix.length());
                break;
            }
        }

        return text;
    }
}
