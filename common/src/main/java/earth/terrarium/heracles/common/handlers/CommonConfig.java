package earth.terrarium.heracles.common.handlers;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefullib.common.lib.Constants;
import earth.terrarium.heracles.Heracles;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class CommonConfig {

    private static final String CONFIG_FILE = "heracles_common_options.json";

    private static Path lastPath;

    public static boolean registerBook = true;
    public static boolean registerUtilities = true;

    public static void load(Path path) {
        CommonConfig.lastPath = path;
        File displayFile = path.resolve(CONFIG_FILE).toFile();
        try {
            if (displayFile.exists()) {
                String configString = FileUtils.readFileToString(displayFile, StandardCharsets.UTF_8);
                JsonObject configObject = Constants.PRETTY_GSON.fromJson(configString, JsonObject.class);
                registerBook = GsonHelper.getAsBoolean(configObject, "registerBook", true);
                registerUtilities = GsonHelper.getAsBoolean(configObject, "registerUtilities", true);
            } else {
                save();
            }
        } catch (Exception e) {
            Heracles.LOGGER.error("Error parsing {}:", CONFIG_FILE, e);
        }
    }

    public static void save() {
        if (lastPath == null) return;
        File displayFile = lastPath.resolve(CONFIG_FILE).toFile();
        JsonObject displayObject = new JsonObject();
        displayObject.addProperty("registerBook", registerBook);
        displayObject.addProperty("registerUtilities", registerUtilities);
        try {
            FileUtils.write(displayFile, Constants.PRETTY_GSON.toJson(displayObject), StandardCharsets.UTF_8);
        } catch (Exception e) {
            Heracles.LOGGER.error("Error saving {}:", CONFIG_FILE, e);
        }
    }
}
