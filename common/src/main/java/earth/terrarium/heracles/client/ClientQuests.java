package earth.terrarium.heracles.client;

import earth.terrarium.heracles.api.Quest;
import net.minecraft.Optionull;
import org.jetbrains.annotations.Nullable;
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
        Quest parent = Optionull.map(quest.parent(), quests::get);

        QuestEntry parentEntry = parent == null ? null : addEntry(quest.parent(), parent, quests);
        QuestEntry entry = new QuestEntry(parentEntry, key, quest, new Vector2d(), new ArrayList<>());

        if (parentEntry == null) {
            ROOTS.add(entry);
        } else {
            parentEntry.children().add(entry);
        }

        return ENTRIES.computeIfAbsent(key, k -> entry);
    }

    public record QuestEntry(@Nullable QuestEntry parent, String key, Quest value, Vector2d position,
                             List<QuestEntry> children) {
    }
}
