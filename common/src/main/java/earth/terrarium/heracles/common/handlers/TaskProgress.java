package earth.terrarium.heracles.common.handlers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.api.tasks.QuestTask;

public class TaskProgress {

    public static final Codec<TaskProgress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("progress").orElse(0).forGetter(TaskProgress::progress),
        Codec.BOOL.fieldOf("complete").orElse(false).forGetter(TaskProgress::isComplete)
    ).apply(instance, TaskProgress::new));

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
