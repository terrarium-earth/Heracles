package earth.terrarium.heracles.api.tasks;

import earth.terrarium.heracles.api.tasks.defaults.*;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import net.minecraft.Optionull;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;

import java.util.IdentityHashMap;
import java.util.Map;

public final class QuestTaskDisplayFormatter {

    private static final Map<QuestTaskType<?>, Formatter<?, ?, ?>> FORMATTERS = new IdentityHashMap<>();

    public static <I, S extends Tag, T extends QuestTask<I, S, T>> void register(QuestTaskType<T> type, Formatter<I, S, T> formatter) {
        FORMATTERS.put(type, formatter);
    }

    @SuppressWarnings("unchecked")
    public static <I, S extends Tag, T extends QuestTask<I, S, T>> Formatter<I, S, T> getFormatter(QuestTaskType<T> type) {
        if (!FORMATTERS.containsKey(type)) {
            return null;
        }
        return (Formatter<I, S, T>) FORMATTERS.get(type);
    }

    public static <T extends Tag> String create(QuestTask<?, T, ?> task, TaskProgress<T> progress) {
        return Optionull.mapOrDefault(getFormatter(task.type()), formatter -> formatter.castAndFormat(progress, task), "");
    }

    static {
        register(KillEntityQuestTask.TYPE, (progress, task) -> String.format("%d/%d", task.storage().read(progress.progress()), task.target()));
        register(GatherItemTask.TYPE, (progress, task) -> String.format("%d/%d", task.storage().read(progress.progress()), task.target()));
        register(AdvancementTask.TYPE, (progress, task) -> String.format("%d/%d", task.storage().read(progress.progress()) ? 1 : 0, 1));
        register(RecipeTask.TYPE, (progress, task) -> String.format("%d/%d", task.storage().read(progress.progress()) ? 1 : 0, 1));
        register(StructureTask.TYPE, (progress, task) -> String.format("%d/%d", task.storage().read(progress.progress()) ? 1 : 0, 1));
        register(BiomeTask.TYPE, (progress, task) -> String.format("%d/%d", task.storage().read(progress.progress()) ? 1 : 0, 1));
        register(BlockInteractTask.TYPE, (progress, task) -> String.format("%d/%d", task.storage().read(progress.progress()) ? 1 : 0, 1));
        register(ChangedDimensionTask.TYPE, (progress, task) -> String.format("%d/%d", task.storage().read(progress.progress()) ? 1 : 0, 1));
        register(CheckTask.TYPE, (progress, task) -> String.format("%d/%d", task.storage().read(progress.progress()) ? 1 : 0, 1));
        register(DummyTask.TYPE, (progress, task) -> String.format("%d/%d", task.storage().read(progress.progress()) ? 1 : 0, 1));
        register(CompositeTask.TYPE, (progress, task) -> String.format("%d/%d", Mth.floor(task.getCompletedTasks(progress.progress())), task.amount()));
        register(XpTask.TYPE, (progress, task) -> String.format("%d/%d", task.storage().read(progress.progress()), task.target()));
        register(EntityInteractTask.TYPE, (progress, task) -> String.format("%d/%d", task.storage().read(progress.progress()) ? 1 : 0, 1));
        register(LocationTask.TYPE, (progress, task) -> String.format("%d/%d", task.storage().read(progress.progress()) ? 1 : 0, 1));
        register(StatTask.TYPE, (progress, task) -> String.format("%d/%d", task.storage().read(progress.progress()), task.target()));
    }

    @FunctionalInterface
    public interface Formatter<I, S extends Tag, T extends QuestTask<I, S, T>> {

        String format(TaskProgress<S> progress, T task);

        @SuppressWarnings("unchecked")
        default String castAndFormat(TaskProgress<S> progress, QuestTask<?, S, ?> task) {
            return format(progress, (T) task);
        }
    }
}
