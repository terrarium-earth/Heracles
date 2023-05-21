package earth.terrarium.heracles.client.handlers;

import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.quests.DeleteQuestPacket;
import earth.terrarium.heracles.common.network.packets.quests.QuestActionPacket;
import earth.terrarium.heracles.common.network.packets.quests.UploadQuestPacket;

import java.util.*;

public class ClientQuests {
    private static final Map<String, QuestEntry> ENTRIES = new HashMap<>();
    private static final Map<String, List<QuestEntry>> BY_GROUPS = new HashMap<>();
    private static final Set<String> DIRTY = new HashSet<>();
    private static final List<String> GROUPS = new ArrayList<>();

    private static final Map<String, QuestProgress> PROGRESS = new HashMap<>();

    public static Optional<QuestEntry> get(String key) {
        return Optional.ofNullable(ENTRIES.get(key));
    }

    public static List<String> groups() {
        return GROUPS;
    }

    public static void sync(Map<String, Quest> quests, List<String> groups) {
        DIRTY.clear();
        ENTRIES.clear();
        GROUPS.clear();

        for (Map.Entry<String, Quest> entry : quests.entrySet()) {
            addEntry(entry.getKey(), entry.getValue(), quests);
        }

        GROUPS.addAll(groups);
        for (QuestEntry value : ENTRIES.values()) {
            BY_GROUPS.computeIfAbsent(value.value.display().group(), k -> new ArrayList<>()).add(value);
        }
    }

    public static void updateProgress(Map<String, QuestProgress> progress) {
        PROGRESS.clear();
        PROGRESS.putAll(progress);
    }

    private static QuestEntry addEntry(
        String key,
        Quest quest,
        Map<String, Quest> quests
    ) {
        List<QuestEntry> dependencies = new ArrayList<>();
        for (String dependency : quest.dependencies()) {
            Quest dependent = quests.get(dependency);
            if (dependent != null && !dependent.dependencies().contains(key) && !key.equals(dependency)) {
                QuestEntry dependencyEntry = addEntry(dependency, dependent, quests);
                if (dependencyEntry != null) {
                    dependencies.add(dependencyEntry);
                }
            }
        }

        QuestEntry entry = new QuestEntry(dependencies, key, quest, new ArrayList<>());

        if (!dependencies.isEmpty()) {
            for (QuestEntry dependency : dependencies) {
                dependency.children().add(entry);
            }
        }

        return ENTRIES.computeIfAbsent(key, k -> entry);
    }

    public static QuestEntry addQuest(String id, Quest quest) {
        QuestEntry entry = new QuestEntry(new ArrayList<>(), id, quest, new ArrayList<>());
        ENTRIES.put(id, entry);
        DIRTY.add(id);
        BY_GROUPS.computeIfAbsent(quest.display().group(), k -> new ArrayList<>()).add(entry);
        return entry;
    }

    public static void removeQuest(QuestEntry entry) {
        ENTRIES.remove(entry.key());
        DIRTY.add(entry.key());
        BY_GROUPS.get(entry.value.display().group()).remove(entry);

        for (QuestEntry child : entry.children()) {
            child.dependencies().remove(entry);
        }

        for (QuestEntry dependency : entry.dependencies()) {
            dependency.children().remove(entry);
        }
    }

    public static void setDirty(String id) {
        DIRTY.add(id);
    }

    public static void sendDirty() {
        for (String id : DIRTY) {
            QuestEntry entry = ENTRIES.get(id);
            if (entry == null) {
                NetworkHandler.CHANNEL.sendToServer(new DeleteQuestPacket(id));
            } else {
                NetworkHandler.CHANNEL.sendToServer(new UploadQuestPacket(id, entry.value));
            }
        }
        NetworkHandler.CHANNEL.sendToServer(new QuestActionPacket(QuestActionPacket.Action.SAVE));
        DIRTY.clear();
    }

    public static Collection<QuestEntry> entries() {
        return ENTRIES.values();
    }

    public static QuestProgress getProgress(String id) {
        return PROGRESS.get(id);
    }

    public record QuestEntry(List<QuestEntry> dependencies, String key, Quest value, List<QuestEntry> children) {}
}
