package earth.terrarium.heracles.client.handlers;

import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskDisplayFormatter;
import earth.terrarium.heracles.api.tasks.client.display.TaskTitleFormatter;
import earth.terrarium.heracles.client.screens.pinned.PinnedDisplay;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.*;

public final class PinnedQuests {

    private static final List<PinnedDisplay> DISPLAY = new ArrayList<>();
    private static final Set<String> COLLAPSED_QUESTS = new HashSet<>();

    private static int height = 0;
    private static int width = 0;

    public static void update(Map<String, QuestProgress> pinnedQuests) {
        DISPLAY.clear();

        Font font = Minecraft.getInstance().font;
        for (var entry : pinnedQuests.entrySet()) {
            ClientQuests.QuestEntry questEntry = ClientQuests.get(entry.getKey()).orElse(null);
            Quest quest = Optionull.map(questEntry, ClientQuests.QuestEntry::value);
            QuestProgress progress = entry.getValue();
            if (quest != null) {
                List<FormattedCharSequence> tasks = new ArrayList<>();
                for (var taskEntry : quest.tasks().entrySet()) {
                    QuestTask<?, ?, ?> task = taskEntry.getValue();
                    var components = ComponentRenderUtils.wrapComponents(TaskTitleFormatter.create(task), 10000, font);
                    Component taskProgress = Component.literal(QuestTaskDisplayFormatter.create(ModUtils.cast(task), progress.getTask(task)));

                    for (int i = 0; i < components.size(); i++) {
                        if (i == 0) {
                            tasks.add(FormattedCharSequence.composite(
                                ConstantComponents.DOT.getVisualOrderText(),
                                components.get(i),
                                ConstantComponents.DASH.getVisualOrderText(),
                                taskProgress.getVisualOrderText()
                            ));
                        } else {
                            tasks.add(FormattedCharSequence.composite(
                                ConstantComponents.EM_DASH.getVisualOrderText(),
                                components.get(i)
                            ));
                        }
                    }
                }
                float completion = calculationCompletion(quest, progress);
                DISPLAY.add(new PinnedDisplay(
                    questEntry,
                    completion,
                    Component.empty()
                        .append(quest.display().title())
                        .append(ConstantComponents.DASH)
                        .append(String.format("%.0f%%", completion * 100)),
                    tasks
                ));
            }
        }

        updateHeight();
        updateWidth();
    }

    private static void updateWidth() {
        PinnedQuests.width = 0;
        Font font = Minecraft.getInstance().font;
        for (PinnedDisplay display : PinnedQuests.display()) {
            PinnedQuests.width = Math.max(PinnedQuests.width, font.width(display.title()) + font.width("▶ "));
            if (!COLLAPSED_QUESTS.contains(display.quest().key())) {
                for (FormattedCharSequence task : display.tasks()) {
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
        updateWidth();
    }
}
