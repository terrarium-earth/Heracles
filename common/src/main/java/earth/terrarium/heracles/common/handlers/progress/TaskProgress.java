package earth.terrarium.heracles.common.handlers.progress;

import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import net.minecraft.nbt.Tag;

public class TaskProgress<S extends Tag> {

    private S progress;
    private boolean complete;

    public TaskProgress(QuestTask<?, S, ?> task) {
        this.progress = task.storage().createDefault();
        this.complete = false;
    }

    public TaskProgress(S progress, boolean complete) {
        this.progress = progress;
        this.complete = complete;
    }

    public <T> void addProgress(QuestTaskType<?> type, QuestTask<T, S, ?> task, T input) {
        if (complete) return;
        progress = task.test(type, progress, input);
        if (task.getProgress(progress) >= 1f) {
            complete = true;
        }
    }

    public boolean isComplete() {
        return complete;
    }

    public S progress() {
        return progress;
    }

    public TaskProgress<S> copy() {
        return new TaskProgress<>(progress, complete);
    }
}
