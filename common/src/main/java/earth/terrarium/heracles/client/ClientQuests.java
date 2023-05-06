package earth.terrarium.heracles.client;

import earth.terrarium.heracles.api.quests.Quest;
import org.joml.Vector2d;

import java.util.*;

public class ClientQuests {
    private static final List<QuestEntry> ROOTS = new ArrayList<>();
    private static final Map<String, QuestEntry> ENTRIES = new HashMap<>();

    public static List<QuestEntry> getRoots() {
        return ROOTS;
    }

    public static Optional<QuestEntry> get(String key) {
        return Optional.ofNullable(ENTRIES.get(key));
    }

    public static void sync(Map<String, Quest> quests) {
        ROOTS.clear();
        ENTRIES.clear();

        for (Map.Entry<String, Quest> entry : quests.entrySet()) {
            addEntry(entry.getKey(), entry.getValue(), quests);
        }
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

        QuestEntry entry = new QuestEntry(dependencies, key, quest, new Vector2d(), new ArrayList<>());

        if (dependencies.isEmpty()) {
            ROOTS.add(entry);
        } else {
            for (QuestEntry dependency : dependencies) {
                dependency.children().add(entry);
            }
        }

        return ENTRIES.computeIfAbsent(key, k -> entry);
    }

    public record QuestEntry(List<QuestEntry> dependencies, String key, Quest value, Vector2d position,
                             List<QuestEntry> children) {
    }
}
