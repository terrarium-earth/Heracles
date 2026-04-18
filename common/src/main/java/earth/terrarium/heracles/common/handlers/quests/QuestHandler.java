package earth.terrarium.heracles.common.handlers.quests;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefullib.common.lib.Constants;
import com.teamresourceful.resourcefullib.common.utils.FileUtils;
import com.teamresourceful.resourcefullib.common.utils.Scheduling;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.tasks.CacheableQuestTaskType;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;

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
    private static final List<String> GROUPS = new ArrayList<>();
    private static final List<String> GROUPS_ORDERS = new ArrayList<>();
    private static final Map<String, GroupSettings> GROUP_SETTINGS = new HashMap<>();
    private static final Map<ResourceLocation, Object> TASK_CACHES = new HashMap<>();
    private static Path lastPath;

    private static final Map<String, ScheduledFuture<?>> SAVING_FUTURES = new HashMap<>();
    private static ScheduledFuture<?> delayedDeletion;

    public static boolean failedToLoad;

    public static void load(RegistryAccess access, Path path) {
        failedToLoad = false;
        Heracles.LOGGER.info("Loading quests");
        Path heraclesPath = path.resolve(Heracles.MOD_ID);
        QuestHandler.lastPath = heraclesPath;
        Path questsPath = heraclesPath.resolve("quests");
        Map<String, Quest> tempQuests = new HashMap<>();
        try {
            Files.createDirectories(questsPath);
            FileUtils.streamFilesAndParse(questsPath, (reader, id) -> load(access, reader, id, tempQuests), FileUtils::isJson);
        } catch (Exception e) {
            Heracles.LOGGER.error("Failed to load quests", e);
            Heracles.LOGGER.error("Quests reverted to last known good state");
            failedToLoad = true;
            return;
        }
        QUESTS.clear();
        QUESTS.putAll(tempQuests);
        QUEST_KEYS.addAll(QUESTS.keySet());
        for (Quest value : QUESTS.values()) {
            value.dependencies().removeIf(Predicate.not(QUESTS::containsKey));
        }
        loadGroups(heraclesPath.resolve("groups.txt").toFile());
        loadGroupSettings(heraclesPath.resolve("group_settings.json").toFile());
        updateTaskCache();
    }

    private static void load(RegistryAccess access, Reader reader, String id, Map<String, Quest> quests) {
        try {
            JsonObject element = Constants.PRETTY_GSON.fromJson(reader, JsonObject.class);
            Quest quest = Quest.CODEC.parse(RegistryOps.create(JsonOps.INSTANCE, access), element).ifError(e -> Heracles.LOGGER.error(e.message())).getOrThrow();
            quest.dependencies().remove(id); // Remove self from dependencies
            quests.put(id, quest);
        } catch (Exception e) {
            Heracles.LOGGER.error("Failed to load quest " + id, e);
        }
    }

    private static void loadGroups(File file) {
        GROUPS.clear();
        GROUPS_ORDERS.clear();
        if (file.exists()) {
            try {
                GROUPS.addAll(org.apache.commons.io.FileUtils.readLines(file, StandardCharsets.UTF_8));
                GROUPS_ORDERS.addAll(GROUPS);
            } catch (Exception e) {
                Heracles.LOGGER.error("Failed to load quest groups", e);
            }
        }
        for (Quest value : QUESTS.values()) {
            for (String s : value.display().groups().keySet()) {
                if (!GROUPS.contains(s)) {
                    GROUPS.add(s);
                    if (!GROUPS_ORDERS.contains(s)) {
                        GROUPS_ORDERS.add(s);
                    }
                }
            }
        }
    }

    private static void loadGroupSettings(File file) {
        GROUP_SETTINGS.clear();
        if (file.exists()) {
            try {
                String content = org.apache.commons.io.FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                JsonObject json = JsonParser.parseString(content).getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                    JsonObject obj = entry.getValue().getAsJsonObject();
                    String iconId = obj.has("icon") ? obj.get("icon").getAsString() : "";
                    boolean iconEnabled = obj.has("iconEnabled") && obj.get("iconEnabled").getAsBoolean();
                    GROUP_SETTINGS.put(entry.getKey(), new GroupSettings(GroupSettings.deserializeIcon(iconId), iconEnabled));
                }
            } catch (Exception e) {
                Heracles.LOGGER.error("Failed to load group settings", e);
            }
        }
    }

    public static void markDirty(String id) {
        try {
            if (lastPath == null) {
                Heracles.LOGGER.error("Failed to mark quest dirty, last path is null");
                return;
            }
            Quest quest = QUESTS.get(id);
            updateTaskCache();
            Path questsPath = lastPath.resolve("quests");
            File file = new File(questsPath.toFile(), pickQuestPath(quest) + "/" + id + ".json");
            JsonElement json = Quest.CODEC.encodeStart(RegistryOps.create(JsonOps.INSTANCE, Heracles.getRegistryAccess()), quest).ifError(e -> Heracles.LOGGER.error(e.message())).getOrThrow();
            if (SAVING_FUTURES.containsKey(id)) {
                SAVING_FUTURES.get(id).cancel(true);
            }
            SAVING_FUTURES.put(id, Scheduling.schedule(() -> {
                try {
                    file.getParentFile().mkdirs();
                    org.apache.commons.io.FileUtils.write(file, Constants.PRETTY_GSON.toJson(json), StandardCharsets.UTF_8);
                } catch (Exception e) {
                    Heracles.LOGGER.error("Failed to save quest " + id, e);
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
            File file = new File(lastPath.toFile(), "groups.txt");
            org.apache.commons.io.FileUtils.writeLines(file, GROUPS);
        } catch (Exception e) {
            Heracles.LOGGER.error("Failed to save quest groups", e);
        }
    }

    public static void saveGroupSettings() {
        if (lastPath == null) {
            return;
        }
        try {
            JsonObject json = new JsonObject();
            for (Map.Entry<String, GroupSettings> entry : GROUP_SETTINGS.entrySet()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("icon", entry.getValue().serializeIcon());
                obj.addProperty("iconEnabled", entry.getValue().iconEnabled());
                json.add(entry.getKey(), obj);
            }
            File file = new File(lastPath.toFile(), "group_settings.json");
            org.apache.commons.io.FileUtils.writeStringToFile(file, Constants.PRETTY_GSON.toJson(json), StandardCharsets.UTF_8);
        } catch (Exception e) {
            Heracles.LOGGER.error("Failed to save group settings", e);
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

    public static void remove(String questId) {
        var quest = QUESTS.get(questId);
        if (quest != null) {
            for (var entry : QUESTS.entrySet()) {
                if (entry.getValue().dependencies().remove(questId)) {
                    markDirty(entry.getKey());
                }
            }
        }
        QUESTS.remove(questId);
        updateTaskCache();
        QUEST_KEYS.remove(questId);
        if (SAVING_FUTURES.containsKey(questId)) SAVING_FUTURES.get(questId).cancel(true);
        SAVING_FUTURES.remove(questId);
        if (delayedDeletion != null) {
            delayedDeletion.cancel(true);
        }
        if (lastPath == null) {
            Heracles.LOGGER.error("Failed to remove dead quest files, last path is null");
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
                Heracles.LOGGER.error("Failed to remove dead quest files", e);
            }
        }, 1500, TimeUnit.MILLISECONDS);
    }

    public static Map<String, Quest> quests() {
        return QUESTS;
    }

    public static List<String> groups() {
        if (GROUPS.isEmpty()) {
            GROUPS.add("Main");
            GROUPS_ORDERS.add("Main");
            saveGroups();
        }
        return GROUPS;
    }

    public static List<String> groupsOrder() {
        return GROUPS_ORDERS;
    }

    public static Map<String, GroupSettings> groupSettings() {
        return GROUP_SETTINGS;
    }

    public static GroupSettings getGroupSettings(String group) {
        return GROUP_SETTINGS.computeIfAbsent(group, k -> new GroupSettings());
    }

    private static void updateTaskCache() {
        TASK_CACHES.clear();
        for (Quest quest : QUESTS.values()) {
            for (QuestTask<?, ?, ?> task : quest.tasks().values()) {
                if (TASK_CACHES.containsKey(task.type().id())) continue;
                if (task.type() instanceof CacheableQuestTaskType<?, ?> cacheable) {
                    Object cache = cacheable.cache(QUESTS.values());
                    TASK_CACHES.put(cacheable.id(), cache);
                } else {
                    TASK_CACHES.put(task.type().id(), null);
                }
            }
        }
    }

    public static <T> T getTaskCache(CacheableQuestTaskType<?, T> type) {
        return ModUtils.cast(TASK_CACHES.get(type.id()));
    }

    public static boolean isTaskUsed(QuestTaskType<?> type) {
        return TASK_CACHES.containsKey(type.id());
    }
}
