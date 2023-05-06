package earth.terrarium.heracles.api.tasks.client;

import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.client.defaults.ItemTaskWidget;
import earth.terrarium.heracles.api.tasks.client.defaults.KillEntityTaskWidget;
import earth.terrarium.heracles.api.tasks.defaults.ItemQuestTask;
import earth.terrarium.heracles.api.tasks.defaults.KillEntityQuestTask;
import earth.terrarium.heracles.common.handlers.TaskProgress;
import net.minecraft.Optionull;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

public final class QuestTaskWidgets {

    private static final Map<QuestTaskType<?>, QuestTaskWidgetFactory<?, ?>> FACTORIES = new IdentityHashMap<>();

    public static <I, T extends QuestTask<I, T>> void register(QuestTaskType<T> type, QuestTaskWidgetFactory<I, T> factory) {
        FACTORIES.put(type, factory);
    }

    @SuppressWarnings("unchecked")
    public static <I, T extends QuestTask<I, T>> QuestTaskWidgetFactory<I, T> getFactory(QuestTaskType<T> type) {
        if (!FACTORIES.containsKey(type)) {
            return null;
        }
        return (QuestTaskWidgetFactory<I, T>) FACTORIES.get(type);
    }

    @Nullable
    public static DisplayWidget create(QuestTask<?, ?> task, TaskProgress progress) {
        return Optionull.map(getFactory(task.type()), factory -> factory.createAndCast(task, progress));
    }

    static {
        register(KillEntityQuestTask.TYPE, KillEntityTaskWidget::new);
        register(ItemQuestTask.TYPE, ItemTaskWidget::new);
    }
}
