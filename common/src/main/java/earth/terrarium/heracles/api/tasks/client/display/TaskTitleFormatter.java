package earth.terrarium.heracles.api.tasks.client.display;

import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import net.minecraft.Optionull;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.IdentityHashMap;
import java.util.Map;

public class TaskTitleFormatter {

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

    public static <T extends Tag> Component create(QuestTask<?, T, ?> task) {
        return Optionull.mapOrDefault(getFormatter(task.type()), formatter -> formatter.castAndFormat(task), CommonComponents.EMPTY);
    }

    static {
        TaskTitleFormatters.init();
    }

    @FunctionalInterface
    public interface Formatter<I, S extends Tag, T extends QuestTask<I, S, T>> {

        Component format(T task);

        @SuppressWarnings("unchecked")
        default Component castAndFormat(QuestTask<?, S, ?> task) {
            return format((T) task);
        }
    }
}
