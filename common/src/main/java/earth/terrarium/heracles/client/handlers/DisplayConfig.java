package earth.terrarium.heracles.client.handlers;

import com.google.gson.JsonObject;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class DisplayConfig {

    private static final String DISPLAY_FILE = "heracles_options.json";

    private static Path lastPath;

    public static int pinnedIndex = 0;
    public static boolean showTutorial = true;

    public static void load(Path path) {
        DisplayConfig.lastPath = path;
        File displayFile = path.resolve(DISPLAY_FILE).toFile();
        try {
            if (displayFile.exists()) {
                String displayString = FileUtils.readFileToString(displayFile, StandardCharsets.UTF_8);
                JsonObject displayObject = ModUtils.PRETTY_GSON.fromJson(displayString, JsonObject.class);
                pinnedIndex = GsonHelper.getAsInt(displayObject, "pinnedIndex", 0);
                showTutorial = GsonHelper.getAsBoolean(displayObject, "showTutorial", true);
            } else {
                save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        if (lastPath == null) return;
        File displayFile = lastPath.resolve(DISPLAY_FILE).toFile();
        JsonObject displayObject = new JsonObject();
        displayObject.addProperty("pinnedIndex", pinnedIndex);
        displayObject.addProperty("showTutorial", showTutorial);
        try {
            FileUtils.write(displayFile, ModUtils.PRETTY_GSON.toJson(displayObject), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
