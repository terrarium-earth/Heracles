package earth.terrarium.heracles.common.handlers.quests;

import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefullib.common.lib.Constants;
import com.teamresourceful.resourcefullib.common.utils.FileUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import org.slf4j.Logger;

import java.io.File;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;

public class QuestHandler {

    private static final Map<String, Quest> QUESTS = HashBiMap.create();
    private static final List<String> GROUPS = new ArrayList<>();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static Path lastPath;
    private static boolean dirty;

    public static boolean failedToLoad;

    public static void load(RegistryAccess access, Path path) {
        failedToLoad = false;
        LOGGER.info("Loading quests");
        Path heraclesPath = path.resolve(Heracles.MOD_ID);
        QuestHandler.lastPath = heraclesPath;
        Path questsPath = heraclesPath.resolve("quests");
        Map<String, Quest> tempQuests = new HashMap<>();
        try {
            Files.createDirectories(questsPath);
            FileUtils.streamFilesAndParse(questsPath, (reader, id) -> load(access, reader, id, tempQuests), FileUtils::isJson);
        } catch (Exception e) {
            LOGGER.error("Failed to load quests", e);
            LOGGER.error("Quests reverted to last known good state");
            failedToLoad = true;
            return;
        }
        QUESTS.clear();
        QUESTS.putAll(tempQuests);
        for (Quest value : QUESTS.values()) {
            value.dependencies().removeIf(Predicate.not(QUESTS::containsKey));
        }
        loadGroups(heraclesPath.resolve("groups.txt").toFile());
    }

    private static void load(RegistryAccess access, Reader reader, String id, Map<String, Quest> quests) {
        try {
            JsonObject element = Constants.PRETTY_GSON.fromJson(reader, JsonObject.class);
            Quest quest = Quest.CODEC.parse(RegistryOps.create(JsonOps.INSTANCE, access), element).getOrThrow(false, LOGGER::error);
            quest.dependencies().remove(id); // Remove self from dependencies
            quests.put(id, quest);
        } catch (Exception e) {
            LOGGER.error("Failed to load quest " + id, e);
        }
    }

    private static void loadGroups(File file) {
        GROUPS.clear();
        if (file.exists()) {
            try {
                GROUPS.addAll(org.apache.commons.io.FileUtils.readLines(file, StandardCharsets.UTF_8));
            } catch (Exception e) {
                LOGGER.error("Failed to load quest groups", e);
            }
        }
        for (Quest value : QUESTS.values()) {
            for (String s : value.display().groups().keySet()) {
                if (!GROUPS.contains(s)) {
                    GROUPS.add(s);
                }
            }
        }
    }

    public static void save() {
        if (lastPath == null || !dirty) {
            return;
        }
        dirty = false;
        if (failedToLoad) {
            LOGGER.error("Failed to load initial quests, not saving");
            return;
        }
        Path questsPath = lastPath.resolve("quests");
        Set<Path> filesWritten = new HashSet<>();
        try {
            for (Map.Entry<String, Quest> entry : QUESTS.entrySet()) {
                try {
                    File file = new File(questsPath.toFile(), pickQuestPath(entry.getValue()) + "/" + entry.getKey() + ".json");
                    filesWritten.add(file.toPath());
                    String json = Constants.PRETTY_GSON.toJson(Quest.CODEC.encodeStart(RegistryOps.create(JsonOps.INSTANCE, Heracles.getRegistryAccess()), entry.getValue())
                        .getOrThrow(false, LOGGER::error));
                    file.getParentFile().mkdirs();
                    org.apache.commons.io.FileUtils.write(file, json, StandardCharsets.UTF_8);
                }catch (Exception e) {
                    LOGGER.error("Failed to save quest " + entry.getKey(), e);
                }
            }

            try (var files = Files.walk(questsPath)) {
                files.filter(Predicate.not(Files::isDirectory))
                    .filter(path -> path.endsWith(".json"))
                    .filter(path -> !filesWritten.contains(path))
                    .forEach(path -> path.toFile().delete());
            }catch (Exception e) {
                LOGGER.error("Failed to delete unused quest files", e);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to save quests", e);
        }
    }

    private static String pickQuestPath(Quest quest) {
        Set<String> groups = quest.display().groups().keySet();
        if (groups.isEmpty()) {
            return "main";
        }
        return ModUtils.findAvailableFolderName(groups.stream()
            .map(s -> s.toLowerCase(Locale.ROOT))
            .map(s -> s.replaceAll("[^a-z0-9]", ""))
            .filter(Predicate.not(String::isBlank))
            .sorted()
            .findFirst()
            .orElse("main"));
    }

    public static Path getQuestPath(Quest quest, String id) {
        return lastPath.resolve("quests/" + pickQuestPath(quest) + "/" + id + ".json");
    }

    public static void saveGroups() {
        if (lastPath == null) {
            return;
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

    public static void upload(String id, Quest quest) {
        QUESTS.put(id, quest);
        dirty = true;
    }

    public static void delete(String id) {
        QUESTS.remove(id);
        dirty = true;
    }

    public static Map<String, Quest> quests() {
        return QUESTS;
    }

    public static List<String> groups() {
        if (GROUPS.isEmpty()) {
            GROUPS.add("Main");
            saveGroups();
        }
        return GROUPS;
    }
}
