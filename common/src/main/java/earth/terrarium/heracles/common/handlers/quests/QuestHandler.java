package earth.terrarium.heracles.common.handlers.quests;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefullib.common.lib.Constants;
import com.teamresourceful.resourcefullib.common.utils.FileUtils;
import com.teamresourceful.resourcefullib.common.utils.Scheduling;
import earth.terrarium.heracles.Heracles;
import static earth.terrarium.heracles.Heracles.LOGGER;

import earth.terrarium.heracles.api.groups.Group;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;

import java.io.File;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class QuestHandler {

    private static final Map<String, Quest> QUESTS = HashBiMap.create();
    private static final Set<String> QUEST_KEYS = Sets.newConcurrentHashSet();
    private static final Map<String, Group> GROUPS = new LinkedHashMap<>();
    private static Path lastPath;

    private static final Map<String, ScheduledFuture<?>> SAVING_FUTURES = new HashMap<>();
    private static ScheduledFuture<?> delayedDeletion;

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
        QUEST_KEYS.addAll(QUESTS.keySet());
        for (Quest value : QUESTS.values()) {
            value.dependencies().removeIf(Predicate.not(QUESTS::containsKey));
        }
        loadGroups(heraclesPath);
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

    private static void loadGroups(Path path) {
        GROUPS.clear();
        File json = path.resolve("groups.json").toFile();
        File txt = path.resolve("groups.txt").toFile();
        boolean save = false;
        if (json.exists()) {
            try {
                Reader reader = Files.newBufferedReader(json.toPath());
                JsonObject element = Constants.PRETTY_GSON.fromJson(reader, JsonObject.class);
                Map<String, Group> groups = Codec.unboundedMap(Codec.STRING, Group.CODEC)
                    .parse(JsonOps.INSTANCE, element)
                    .getOrThrow(false, LOGGER::error);
                GROUPS.putAll(groups);
            } catch (Exception e) {
                LOGGER.error("Failed to load quest groups", e);
            }
        } else if (txt.exists()) {
            try {
                for (String group : org.apache.commons.io.FileUtils.readLines(txt, StandardCharsets.UTF_8)) {
                    GROUPS.put(group, new Group(group));
                }
                Files.deleteIfExists(txt.toPath());
                save = true;
            } catch (Exception e) {
                LOGGER.error("Failed to load quest groups", e);
            }
        }
        for (Quest value : QUESTS.values()) {
            for (String s : value.display().groups().keySet()) {
                if (!GROUPS.containsKey(s)) {
                    GROUPS.put(s, new Group(s));
                }
            }
        }
        if (save) {
            saveGroups();
        }
    }

    public static void markDirty(String id) {
        try {
            if (lastPath == null) {
                LOGGER.error("Failed to mark quest dirty, last path is null");
                return;
            }
            Quest quest = QUESTS.get(id);
            Path questsPath = lastPath.resolve("quests");
            File file = new File(questsPath.toFile(), pickQuestPath(quest) + "/" + id + ".json");
            JsonElement json = Quest.CODEC.encodeStart(RegistryOps.create(JsonOps.INSTANCE, Heracles.getRegistryAccess()), quest)
                .getOrThrow(false, LOGGER::error);
            if (SAVING_FUTURES.containsKey(id)) {
                SAVING_FUTURES.get(id).cancel(true);
            }
            SAVING_FUTURES.put(id, Scheduling.schedule(() -> {
                try {
                    file.getParentFile().mkdirs();
                    org.apache.commons.io.FileUtils.write(file, Constants.PRETTY_GSON.toJson(json), StandardCharsets.UTF_8);
                } catch (Exception e) {
                    LOGGER.error("Failed to save quest " + id, e);
                }
            }, 1500, TimeUnit.MILLISECONDS));
        } catch (Exception ignored) {}
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
            File file = new File(lastPath.toFile(), "groups.json");
            JsonElement json = Codec.unboundedMap(Codec.STRING, Group.CODEC)
                .encodeStart(JsonOps.INSTANCE, GROUPS)
                .getOrThrow(false, LOGGER::error);
            org.apache.commons.io.FileUtils.write(file, Constants.PRETTY_GSON.toJson(json), StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.error("Failed to save quest groups", e);
        }
    }

    public static Quest get(String id) {
        return QUESTS.get(id);
    }

    public static void upload(String id, Quest quest) {
        QUESTS.put(id, quest);
        QUEST_KEYS.add(id);
        markDirty(id);
    }

    public static void remove(String quest) {
        QUESTS.remove(quest);
        QUEST_KEYS.remove(quest);
        if (SAVING_FUTURES.containsKey(quest)) SAVING_FUTURES.get(quest).cancel(true);
        SAVING_FUTURES.remove(quest);
        if (delayedDeletion != null) {
            delayedDeletion.cancel(true);
        }
        if (lastPath == null) {
            LOGGER.error("Failed to remove dead quest files, last path is null");
            return;
        }
        Path questsPath = lastPath.resolve("quests");
        delayedDeletion = Scheduling.schedule(() -> {
            try {
                try (var files = Files.walk(questsPath)) {
                    files.filter(Predicate.not(Files::isDirectory))
                        .filter(FileUtils::isJson)
                        .forEach(path -> {
                            String id = path.getFileName().toString().replace(".json", "");
                            if (!QUEST_KEYS.contains(id)) {
                                path.toFile().delete();
                            }
                        });
                }
            } catch (Exception e) {
                LOGGER.error("Failed to remove dead quest files", e);
            }
        }, 1500, TimeUnit.MILLISECONDS);
    }

    public static Map<String, Quest> quests() {
        return QUESTS;
    }

    public static Map<String, Group> groups() {
        if (GROUPS.isEmpty()) {
            GROUPS.put("main", new Group("Main"));
            saveGroups();
        }
        return GROUPS;
    }
}
