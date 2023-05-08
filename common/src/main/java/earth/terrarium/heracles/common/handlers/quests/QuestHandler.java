package earth.terrarium.heracles.common.handlers.quests;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefullib.common.lib.Constants;
import com.teamresourceful.resourcefullib.common.utils.FileUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import org.slf4j.Logger;

import java.io.File;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuestHandler {

    private static final BiMap<String, Quest> QUESTS = HashBiMap.create();
    private static final List<String> GROUPS = new ArrayList<>();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static Path lastPath;

    public static void load(Path path) {
        Path heraclesPath = path.resolve(Heracles.MOD_ID);
        QuestHandler.lastPath = heraclesPath;
        Path questsPath = heraclesPath.resolve("quests");
        try {
            Files.createDirectories(questsPath);
            FileUtils.streamFilesAndParse(questsPath, QuestHandler::load, FileUtils::isJson);
        } catch (Exception e) {
            LOGGER.error("Failed to load quests", e);
        }
        loadGroups(heraclesPath.resolve("groups.txt").toFile());
    }

    private static void load(Reader reader, String id) {
        try {
            JsonObject element = Constants.GSON.fromJson(reader, JsonObject.class);
            Quest quest = Quest.CODEC.parse(JsonOps.INSTANCE, element).getOrThrow(false, LOGGER::error);
            QUESTS.put(id, quest);
        } catch (Exception e) {
            LOGGER.error("Failed to load quest " + id, e);
        }
    }

    private static void loadGroups(File file) {
        GROUPS.clear();
        if (!file.exists()) {
            try {
                GROUPS.addAll(org.apache.commons.io.FileUtils.readLines(file, StandardCharsets.UTF_8));
            } catch (Exception e) {
                LOGGER.error("Failed to load quest groups", e);
            }
        }
        for (Quest value : QUESTS.values()) {
            if (!GROUPS.contains(value.display().group())) {
                GROUPS.add(value.display().group());
            }
        }
    }

    private static void save() {
        if (lastPath == null) {
            return;
        }
        Path questsPath = lastPath.resolve("quests");
        try {
            org.apache.commons.io.FileUtils.deleteDirectory(questsPath.toFile());
            for (Map.Entry<String, Quest> entry : QUESTS.entrySet()) {
                File file = new File(questsPath.toFile(), entry.getKey() + ".json");
                String json = Constants.GSON.toJson(Quest.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue())
                    .getOrThrow(false, LOGGER::error));
                org.apache.commons.io.FileUtils.write(file, json, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to save quests", e);
        }
        try {
            File file = new File(lastPath.toFile(), "groups.txt");
            org.apache.commons.io.FileUtils.writeLines(file, GROUPS);
        } catch (Exception e) {
            LOGGER.error("Failed to save quest groups", e);
        }
    }

    public static Quest get(String id) {
        return QUESTS.get(id);
    }

    public static String getKey(Quest quest) {
        return QUESTS.inverse().get(quest);
    }

    public static Map<String, Quest> quests() {
        return QUESTS;
    }

    public static List<String> groups() {
        return GROUPS;
    }
}
