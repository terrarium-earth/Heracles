package earth.terrarium.heracles.client.handlers;

import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.display.QuestTaskDisplayFormatter;
import earth.terrarium.heracles.api.tasks.display.TaskTitleFormatter;
import earth.terrarium.heracles.client.screens.pinned.PinnedDisplay;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import java.util.*;

public final class PinnedQuests {

    private static final List<PinnedDisplay> DISPLAY = new ArrayList<>();
    private static final Set<String> COLLAPSED_QUESTS = new HashSet<>();

    private static int height = 0;
    private static int width = 0;

    public static void update(Map<String, QuestProgress> pinnedQuests) {
        DISPLAY.clear();

        for (var entry : pinnedQuests.entrySet()) {
            ClientQuests.QuestEntry questEntry = ClientQuests.get(entry.getKey()).orElse(null);
            Quest quest = Optionull.map(questEntry, ClientQuests.QuestEntry::value);
            QuestProgress progress = entry.getValue();
            if (quest != null) {
                List<Component> tasks = new ArrayList<>();
                for (var taskEntry : quest.tasks().entrySet()) {
                    QuestTask<?, ?, ?> task = taskEntry.getValue();
                    Component taskTitle = TaskTitleFormatter.create(task);
                    Component taskProgress = Component.literal(QuestTaskDisplayFormatter.create(ModUtils.cast(task), progress.getTask(task)));
                    tasks.add(Component.literal(" • ").append(taskTitle).append(Component.literal(" - ")).append(taskProgress));
                }
                float completion = calculationCompletion(quest, progress);
                DISPLAY.add(new PinnedDisplay(
                    questEntry,
                    completion,
                    Component.empty()
                        .append(quest.display().title())
                        .append(Component.literal(" - "))
                        .append(String.format("%.0f%%", completion * 100)),
                    tasks
                ));
            }
        }

        updateHeight();

        Font font = Minecraft.getInstance().font;

        for (PinnedDisplay display : PinnedQuests.display()) {
            PinnedQuests.width = Math.max(PinnedQuests.width, font.width(display.title()) + font.width("▶ "));
            if (!COLLAPSED_QUESTS.contains(display.quest().key())) {
                for (Component task : display.tasks()) {
                    PinnedQuests.width = Math.max(PinnedQuests.width, font.width(task));
                }
            }
        }
    }

    private static void updateHeight() {
        PinnedQuests.height = 11;
        for (PinnedDisplay display : PinnedQuests.display()) {
            PinnedQuests.height += 9;
            if (!COLLAPSED_QUESTS.contains(display.quest().key())) {
                PinnedQuests.height += display.tasks().size() * 9;
            }
        }
    }

    private static float calculationCompletion(Quest quest, QuestProgress progress) {
        if (progress.isComplete()) return 1f;
        float completion = 0;
        for (var task : quest.tasks().values()) {
            completion += progress.getTask(task).isComplete() ? 1 : 0;
        }
        return completion / quest.tasks().size();
    }

    public static List<PinnedDisplay> display() {
        return DISPLAY;
    }

    public static int height() {
        return height;
    }

    public static int width() {
        return width;
    }

    public static boolean isCollapsed(String questKey) {
        return COLLAPSED_QUESTS.contains(questKey);
    }

    public static void toggleCollapse(String questKey) {
        if (COLLAPSED_QUESTS.contains(questKey)) {
            COLLAPSED_QUESTS.remove(questKey);
        } else {
            COLLAPSED_QUESTS.add(questKey);
        }
        updateHeight();
    }
}
