package earth.terrarium.heracles.api.events;

import earth.terrarium.heracles.api.quests.Quest;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * (Unstable API) Server-side {@link Quest} lifecycle events.
 *
 * @apiNote This API comes with no support nor stability guarantees whatsoever. Use at your own risk.
 */
@ApiStatus.Experimental
public final class HeraclesEvents {
    private static final List<QuestCompleteListener> QUEST_PROGRESS = new ArrayList<>();
    private static final List<TaskCompleteListener> TASK_PROGRESS = new ArrayList<>();

    private HeraclesEvents() {
        throw new UnsupportedOperationException();
    }

    /**
     * Invoked after a {@link Quest} status change.
     */
    @FunctionalInterface
    public interface QuestCompleteListener {
        void accept(QuestEventTarget target);

        static void fire(QuestEventTarget target) {
            QUEST_PROGRESS.forEach(l -> l.accept(target));
        }

        static void register(QuestCompleteListener listener) {
            QUEST_PROGRESS.add(listener);
        }

        static void unregister(QuestCompleteListener listener) {
            QUEST_PROGRESS.remove(listener);
        }
    }

    @FunctionalInterface
    public interface TaskCompleteListener {
        void accept(TaskEventTarget event);

        static void fire(TaskEventTarget target) {
            TASK_PROGRESS.forEach(l -> l.accept(target));
        }

        static void register(TaskCompleteListener listener) {
            TASK_PROGRESS.add(listener);
        }

        static void unregister(TaskCompleteListener listener) {
            TASK_PROGRESS.remove(listener);
        }
    }
}
