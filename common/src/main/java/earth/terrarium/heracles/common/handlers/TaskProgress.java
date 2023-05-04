package earth.terrarium.heracles.common.handlers;

import earth.terrarium.heracles.api.tasks.QuestTask;

public class TaskProgress {

    private int progress;
    private boolean complete;

    public TaskProgress() {
        this.progress = 0;
        this.complete = false;
    }

    public TaskProgress(int progress, boolean complete) {
        this.progress = progress;
        this.complete = complete;
    }

    public <T> void addProgress(QuestTask<T, ?> task, T input) {
        if (complete) return;
        progress += task.test(input);
        if (progress >= task.target()) {
            complete = true;
        }
    }

    public boolean isComplete() {
        return complete;
    }

    public int progress() {
        return progress;
    }

    public TaskProgress copy() {
        return new TaskProgress(progress, complete);
    }
}
