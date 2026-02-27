package earth.terrarium.heracles.client.handlers;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefullib.common.lib.Constants;
import earth.terrarium.heracles.Heracles;
import net.minecraft.ChatFormatting;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Locale;

public class DisplayConfig {

    private static final String DISPLAY_FILE = "heracles_options.json";

    private static Path lastPath;

    public static int pinnedIndex = 0;
    public static int maxEditorHistory = 100;
    public static boolean showTutorial = true;
    public static ChatFormatting editorColor = ChatFormatting.WHITE;
    public static boolean snapToGrid = false;

    public static void load(Path path) {
        DisplayConfig.lastPath = path;
        File displayFile = path.resolve(DISPLAY_FILE).toFile();
        try {
            if (displayFile.exists()) {
                String displayString = FileUtils.readFileToString(displayFile, StandardCharsets.UTF_8);
                JsonObject displayObject = Constants.PRETTY_GSON.fromJson(displayString, JsonObject.class);
                pinnedIndex = GsonHelper.getAsInt(displayObject, "pinnedIndex", 0);
                showTutorial = GsonHelper.getAsBoolean(displayObject, "showTutorial", true);
                maxEditorHistory = GsonHelper.getAsInt(displayObject, "maxEditorHistory", 100);
                editorColor = getEnum(displayObject, "editorColor", ChatFormatting.class, ChatFormatting.WHITE);
                snapToGrid = GsonHelper.getAsBoolean(displayObject, "snapToGrid", false);
            } else {
                save();
            }
        } catch (Exception e) {
            Heracles.LOGGER.error("Error parsing {}:", DISPLAY_FILE, e);
        }
    }

    public static void save() {
        if (lastPath == null) return;
        File displayFile = lastPath.resolve(DISPLAY_FILE).toFile();
        JsonObject displayObject = new JsonObject();
        displayObject.addProperty("pinnedIndex", pinnedIndex);
        displayObject.addProperty("showTutorial", showTutorial);
        displayObject.addProperty("maxEditorHistory", maxEditorHistory);
        displayObject.addProperty("editorColor", editorColor.getName());
        displayObject.addProperty("snapToGrid", snapToGrid);
        try {
            FileUtils.write(displayFile, Constants.PRETTY_GSON.toJson(displayObject), StandardCharsets.UTF_8);
        } catch (Exception e) {
            Heracles.LOGGER.error("Error saving {}:", DISPLAY_FILE, e);
        }
    }

    private static <T extends Enum<T>> T getEnum(JsonObject object, String key, Class<T> clazz, T defaultValue) {
        if (object.has(key)) {
            return Enum.valueOf(clazz, GsonHelper.getAsString(object, key).toUpperCase(Locale.ROOT));
        }
        return defaultValue;
    }
}
