package earth.terrarium.heracles.common.handlers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefullib.common.lib.Constants;
import com.teamresourceful.resourcefullib.common.utils.FileUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.Quest;
import org.slf4j.Logger;

import java.io.Reader;
import java.nio.file.Path;
import java.util.Map;

public class QuestHandler {

    private static final BiMap<String, Quest> QUESTS = HashBiMap.create();
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void load(Path path) {
        Path heraclesPath = path.resolve(Heracles.MOD_ID);
        Path questsPath = heraclesPath.resolve("quests");
        if (!questsPath.toFile().exists()) {
            questsPath.toFile().mkdirs();
        }
        FileUtils.streamFilesAndParse(questsPath, QuestHandler::load, FileUtils::isJson);
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

    public static Quest get(String id) {
        return QUESTS.get(id);
    }

    public static String getKey(Quest quest) {
        return QUESTS.inverse().get(quest);
    }

    public static Map<String, Quest> quests() {
        return QUESTS;
    }
}
