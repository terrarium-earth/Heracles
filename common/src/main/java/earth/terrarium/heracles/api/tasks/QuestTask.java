package earth.terrarium.heracles.api.tasks;

public interface QuestTask<I, T extends QuestTask<I, T>> {

    /**
     * The id of the task.
     *
     * @return The id.
     */
    String id();

    /**
     * The maximum progress of the task.
     *
     * @return The maximum progress.
     */
    int target();

    /**
     * Checks if the task is progressing.
     *
     * @param input The input to test.
     * @return The added progress.
     */
    int test(I input);

    QuestTaskType<T> type();

}
