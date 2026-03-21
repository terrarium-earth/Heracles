package earth.terrarium.heracles.client.handlers;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.menus.quests.QuestsContent;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.quests.ServerboundUpdateQuestPacket;
import earth.terrarium.heracles.common.network.packets.quests.data.NetworkQuestData;
import earth.terrarium.heracles.common.utils.ModUtils;

import java.util.*;
import java.util.function.Function;

public class ClientQuests {
    private static final Map<String, QuestEntry> ENTRIES = new HashMap<>();
    private static final Map<String, ModUtils.QuestStatus> STATUS = new HashMap<>();
    private static final Map<String, List<QuestEntry>> BY_GROUPS = new HashMap<>();
    private static final List<String> GROUPS = new ArrayList<>();

    private static final Map<String, QuestProgress> PROGRESS = new HashMap<>();

    public static Optional<QuestEntry> get(String key) {
        return Optional.ofNullable(ENTRIES.get(key));
    }

    public static Optional<Quest> getQuest(String key) {
        return get(key).map(QuestEntry::value);
    }

    public static List<String> groups() {
        return GROUPS;
    }

    public static void sync(Map<String, Quest> quests, List<String> groups) {
        Heracles.LOGGER.debug("Synced quests");
        Heracles.LOGGER.debug("Quests: {}", quests.keySet());
        Heracles.LOGGER.debug("Groups: {}", groups);
        ENTRIES.clear();
        BY_GROUPS.clear();
        GROUPS.clear();

        for (Map.Entry<String, Quest> entry : quests.entrySet()) {
            addEntry(entry.getKey(), entry.getValue(), quests);
        }

        GROUPS.addAll(groups);
        for (QuestEntry value : ENTRIES.values()) {
            for (String s : value.value.display().groups().keySet()) {
                BY_GROUPS.computeIfAbsent(s, k -> new ArrayList<>()).add(value);
            }
        }
    }

    public static void syncDescriptions(Map<String, String> descriptions) {
        for (Map.Entry<String, String> entry : descriptions.entrySet()) {
            get(entry.getKey())
                .map(QuestEntry::value)
                .map(Quest::display)
                .ifPresent(display -> display.setDescription(List.of(entry.getValue().split("\n"))));
        }
    }

    public static void mergeProgress(Map<String, QuestProgress> progress) {
        PROGRESS.putAll(progress);
    }

    private static QuestEntry addEntry(
        String key,
        Quest quest,
        Map<String, Quest> quests
    ) {
        QuestEntry existingEntry = ENTRIES.get(key);

        if (existingEntry != null) {
            return existingEntry;
        }

        List<QuestEntry> dependencies = new ArrayList<>();

        for (String dependency : quest.dependencies()) {
            Quest dependent = quests.get(dependency);

            if (dependent != null && !dependent.dependencies().contains(key) && !key.equals(dependency)) {
                dependencies.add(addEntry(dependency, dependent, quests));
            }
        }

        QuestEntry entry = new QuestEntry(key, quest, dependencies);

        dependencies.forEach(dependency -> dependency.dependents().add(entry));

        ENTRIES.put(key, entry);
        return entry;
    }

    public static void remove(String id) {
        QuestEntry quest = ENTRIES.remove(id);
        BY_GROUPS.values().forEach(list -> list.removeIf(entry -> entry.key().equals(id)));
        if (quest != null) {
            for (QuestEntry dependency : quest.dependencies()) {
                dependency.dependents().remove(quest);
            }
            for (QuestEntry child : quest.dependents()) {
                child.dependencies().remove(quest);
            }
        }
    }

    public static QuestEntry addQuest(String id, Quest quest) {
        remove(id);
        QuestEntry entry = new QuestEntry(id, quest);
        for (String dependency : quest.dependencies()) {
            QuestEntry dependent = ENTRIES.get(dependency);
            if (dependent != null) {
                entry.dependencies().add(dependent);
                dependent.dependents().removeIf(child -> child.key().equals(id));
                dependent.dependents().add(entry);
            }
        }
        for (QuestEntry value : ENTRIES.values()) {
            if (value.value.dependencies().contains(id)) {
                entry.dependents().add(value);
                value.dependencies().removeIf(dependency -> dependency.key().equals(id));
                value.dependencies().add(entry);
            }
        }
        ENTRIES.put(id, entry);
        for (String s : quest.display().groups().keySet()) {
            BY_GROUPS.computeIfAbsent(s, k -> new ArrayList<>()).add(entry);
        }
        return entry;
    }

    public static Collection<QuestEntry> entries() {
        return ENTRIES.values();
    }

    public static QuestProgress getProgress(String id) {
        return PROGRESS.get(id);
    }

    public static Optional<ModUtils.QuestStatus> getStatus(String id) {
        return Optional.ofNullable(STATUS.get(id));
    }

    public static List<QuestEntry> byGroup(String group) {
        return BY_GROUPS.getOrDefault(group, List.of());
    }

    public static void updateQuest(QuestEntry entry, Function<Quest, NetworkQuestData.Builder> supplier) {
        ClientQuests.updateQuest(entry, supplier, true);
    }

    public static void updateQuest(QuestEntry entry, Function<Quest, NetworkQuestData.Builder> supplier, boolean sendToSelf) {
        if (entry == null) return;
        ClientQuests.updateQuest(entry, supplier.apply(entry.value()), sendToSelf);
    }

    public static void updateQuest(QuestEntry entry, NetworkQuestData.Builder builder) {
        ClientQuests.updateQuest(entry, builder, true);
    }

    public static void updateQuest(QuestEntry entry, NetworkQuestData.Builder builder, boolean sendToSelf) {
        NetworkQuestData data = builder.build();
        data.update(entry.value());
        NetworkHandler.CHANNEL.sendToServer(new ServerboundUpdateQuestPacket(entry.key(), data, sendToSelf));
    }

    public static void syncGroup(QuestsContent content) {
        STATUS.putAll(content.quests());
    }

    public record QuestEntry(String key, Quest value, List<QuestEntry> dependencies, List<QuestEntry> dependents) {

        public QuestEntry(String key, Quest value, List<QuestEntry> dependencies) {
            this(key, value, dependencies, new ArrayList<>());
        }

        public QuestEntry(String key, Quest value) {
            this(key, value, new ArrayList<>());
        }

        @Override
        public String toString() {
            return "QuestEntry{ key='" + key + "', value=" + value + " }";
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof QuestEntry) {
                return key.equals(((QuestEntry) obj).key);
            }
            return false;
        }
    }
}
