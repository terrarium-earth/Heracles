package earth.terrarium.heracles.common.handlers.progress;

import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import net.minecraft.nbt.Tag;

import java.util.function.Supplier;

public class TaskProgress<S extends Tag> {

    private S progress;
    private final Supplier<S> defaultProgress;
    private boolean complete;

    public TaskProgress(QuestTask<?, S, ?> task) {
        this.progress = task.storage().createDefault();
        this.defaultProgress = task.storage()::createDefault;
        this.complete = false;
    }

    public TaskProgress(S progress, Supplier<S> defaultProgress, boolean complete) {
        this.progress = progress;
        this.defaultProgress = defaultProgress;
        this.complete = complete;
    }

    public <T> void addProgress(QuestTaskType<?> type, QuestTask<T, S, ?> task, T input) {
        if (complete) return;
        progress = task.test(type, progress, input);
        updateComplete(task);
    }

    public void resetProgress() {
        progress = defaultProgress.get();
        setComplete(false);
    }

    public boolean isComplete() {
        return complete;
    }

    public void updateComplete(QuestTask<?, S, ?> task) {
        if (complete) return;
        if (task.getProgress(progress) >= 1f) {
            setComplete(true);
        }
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public S progress() {
        return progress;
    }

    public TaskProgress<S> copy() {
        return new TaskProgress<>(progress, defaultProgress, complete);
    }
}
