package earth.terrarium.heracles.common.handlers.progress;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

public class TaskProgress<S extends Tag> {

    public static final Codec<Tag> TAG_CODEC = Codec.PASSTHROUGH.comapFlatMap(dynamic -> {
        return DataResult.success(dynamic.convert(NbtOps.INSTANCE).getValue());
    }, tag -> new Dynamic<>(NbtOps.INSTANCE, tag));

    public static Codec<TaskProgress<?>> codec(QuestTask<?, ?, ?> task) {
        return RecordCodecBuilder.create(instance -> instance.group(
            TAG_CODEC.fieldOf("progress").orElse(task.storage().createDefault()).forGetter(TaskProgress::progress),
            Codec.BOOL.fieldOf("complete").orElse(false).forGetter(TaskProgress::isComplete)
        ).apply(instance, TaskProgress::new));
    }

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
