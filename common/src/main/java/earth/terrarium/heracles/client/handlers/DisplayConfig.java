package earth.terrarium.heracles.client.handlers;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefullib.common.lib.Constants;
import earth.terrarium.heracles.Heracles;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class DisplayConfig {

    private static Path lastPath;

    public static int pinnedIndex = 0;

    public static void load(Path path) {
        Path heraclesPath = path.resolve(Heracles.MOD_ID);
        DisplayConfig.lastPath = heraclesPath;
        File displayFile = heraclesPath.resolve("display.json").toFile();
        try {
            Files.createDirectories(heraclesPath);
            if (displayFile.exists()) {
                String displayString = FileUtils.readFileToString(displayFile, StandardCharsets.UTF_8);
                JsonObject displayObject = Constants.GSON.fromJson(displayString, JsonObject.class);
                pinnedIndex = GsonHelper.getAsInt(displayObject, "pinnedIndex", 0);
            } else {
                save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        if (lastPath == null) return;
        File displayFile = lastPath.resolve("display.json").toFile();
        JsonObject displayObject = new JsonObject();
        displayObject.addProperty("pinnedIndex", pinnedIndex);
        try {
            FileUtils.write(displayFile, Constants.GSON.toJson(displayObject), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
