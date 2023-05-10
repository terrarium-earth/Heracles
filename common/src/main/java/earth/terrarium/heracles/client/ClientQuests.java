package earth.terrarium.heracles.client;

import earth.terrarium.heracles.api.quests.Quest;

import java.util.*;

public class ClientQuests {
    private static final Map<String, QuestEntry> ENTRIES = new HashMap<>();
    private static final List<String> GROUPS = new ArrayList<>();

    public static Optional<QuestEntry> get(String key) {
        return Optional.ofNullable(ENTRIES.get(key));
    }

    public static List<String> groups() {
        return GROUPS;
    }

    public static void sync(Map<String, Quest> quests, List<String> groups) {
        ENTRIES.clear();
        GROUPS.clear();

        for (Map.Entry<String, Quest> entry : quests.entrySet()) {
            addEntry(entry.getKey(), entry.getValue(), quests);
        }

        GROUPS.addAll(groups);
    }

    private static QuestEntry addEntry(
        String key,
        Quest quest,
        Map<String, Quest> quests
    ) {
        List<QuestEntry> dependencies = new ArrayList<>();
        for (String dependency : quest.dependencies()) {
            Quest dependent = quests.get(dependency);
            if (dependent != null) {
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

    public record QuestEntry(List<QuestEntry> dependencies, String key, Quest value, List<QuestEntry> children) {}
}
