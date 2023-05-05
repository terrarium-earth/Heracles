package earth.terrarium.heracles.api.tasks.client;

import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.common.handlers.TaskProgress;

public interface QuestTaskWidgetFactory<I, T extends QuestTask<I, T>> {

    QuestTaskWidget create(T task, TaskProgress progress);

    default QuestTaskWidget createAndCast(QuestTask<?, ?> task, TaskProgress progress) {
        return create(this.cast(task), progress);
    }

    @SuppressWarnings("unchecked")
    default T cast(QuestTask<?, ?> task) {
        return (T) task;
    }
}
