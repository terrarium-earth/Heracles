package earth.terrarium.heracles.api.tasks;

import earth.terrarium.heracles.api.tasks.defaults.ItemQuestTask;
import earth.terrarium.heracles.api.tasks.defaults.KillEntityQuestTask;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import net.minecraft.Optionull;

import java.util.IdentityHashMap;
import java.util.Map;

public final class QuestTaskDisplayFormatter {

    private static final Map<QuestTaskType<?>, Formatter<?, ?>> FORMATTERS = new IdentityHashMap<>();

    public static <I, T extends QuestTask<I, T>> void register(QuestTaskType<T> type, Formatter<I, T> formatter) {
        FORMATTERS.put(type, formatter);
    }

    @SuppressWarnings("unchecked")
    public static <I, T extends QuestTask<I, T>> Formatter<I, T> getFormatter(QuestTaskType<T> type) {
        if (!FORMATTERS.containsKey(type)) {
            return null;
        }
        return (Formatter<I, T>) FORMATTERS.get(type);
    }

    public static String create(QuestTask<?, ?> task, TaskProgress progress) {
        return Optionull.mapOrDefault(getFormatter(task.type()), formatter -> formatter.castAndFormat(progress, task), "");
    }

    static {
        register(KillEntityQuestTask.TYPE, (progress, task) -> String.format("%d/%d", task.storage().read(progress.progress()), task.target()));
        register(ItemQuestTask.TYPE, (progress, task) -> String.format("%d/%d", task.storage().read(progress.progress()), task.target()));
    }

    @FunctionalInterface
    public interface Formatter<I, T extends QuestTask<I, T>> {

        String format(TaskProgress progress, T task);

        @SuppressWarnings("unchecked")
        default String castAndFormat(TaskProgress progress, QuestTask<?, ?> task) {
            return format(progress, (T) task);
        }
    }
}
