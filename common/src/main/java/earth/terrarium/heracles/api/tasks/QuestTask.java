package earth.terrarium.heracles.api.tasks;

import earth.terrarium.heracles.api.tasks.storage.TaskStorage;
import net.minecraft.nbt.Tag;

public interface QuestTask<I, S extends Tag, T extends QuestTask<I, S, T>> {

    /**
     * The id of the task.
     *
     * @return The id.
     */
    String id();

    /**
     * Checks if the task is progressing.
     *
     * @param progress The current progress of the task.
     * @param input The input to test.
     * @return The added progress.
     */
    S test(S progress, I input);


    /**
     * Gets the progress of the task.
     *
     * @param progress The current progress of the task.
     * @return The progress from 0 to 1.
     */
    float getProgress(S progress);

    /**
     * Gets the storage of the task.
     *
     * @return The storage.
     */
    TaskStorage<?, S> storage();

    default boolean isCompatibleWith(QuestTaskType<?> type) {
        return type() == type;
    }

    /**
     * Gets the type of the task.
     *
     * @return The type.
     */
    QuestTaskType<T> type();

}
