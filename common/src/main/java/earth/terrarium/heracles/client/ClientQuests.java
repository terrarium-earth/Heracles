package earth.terrarium.heracles.client;

import earth.terrarium.heracles.api.Quest;
import net.minecraft.Optionull;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClientQuests {
    private static final List<QuestEntry> ROOTS = new ArrayList<>();
    private static final Map<ResourceLocation, QuestEntry> ENTRIES = new HashMap<>();

    public static List<QuestEntry> getRoots() {
        return ROOTS;
    }

    public static Optional<QuestEntry> get(ResourceLocation key) {
        return Optional.ofNullable(ENTRIES.get(key));
    }

    public static void sync(Map<ResourceLocation, Quest> quests) {
        ROOTS.clear();
        ENTRIES.clear();

        for (Map.Entry<ResourceLocation, Quest> entry : quests.entrySet()) {
            addEntry(entry.getKey(), entry.getValue(), quests);
        }
    }

    private static QuestEntry addEntry(
        ResourceLocation key,
        Quest quest,
        Map<ResourceLocation, Quest> quests
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

    public record QuestEntry(@Nullable QuestEntry parent, ResourceLocation key, Quest value, Vector2d position,
                             List<QuestEntry> children) {
    }
}
