package earth.terrarium.heracles.api.tasks;

import earth.terrarium.heracles.api.tasks.storage.TaskStorage;
import net.minecraft.nbt.Tag;

public interface QuestTask<I, T extends QuestTask<I, T>> {

    /**
     * The id of the task.
     *
     * @return The id.
     */
    String id();

    /**
     * Checks if the task is progressing.
     *
     * @param input The input to test.
     * @return The added progress.
     */
    Tag test(Tag progress, I input);


    float getProgress(Tag progress);

    TaskStorage<?> storage();

    QuestTaskType<T> type();

}
