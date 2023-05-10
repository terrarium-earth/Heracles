package earth.terrarium.heracles.common.handlers.progress;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.api.tasks.QuestTask;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

public class TaskProgress {

    public static final Codec<Tag> TAG_CODEC = Codec.PASSTHROUGH.comapFlatMap(dynamic -> {
        return DataResult.success(dynamic.convert(NbtOps.INSTANCE).getValue());
    }, tag -> new Dynamic<>(NbtOps.INSTANCE, tag));

    public static Codec<TaskProgress> codec(QuestTask<?, ?> task) {
        return RecordCodecBuilder.create(instance -> instance.group(
            TAG_CODEC.fieldOf("progress").orElse(task.storage().createDefault()).forGetter(TaskProgress::progress),
            Codec.BOOL.fieldOf("complete").orElse(false).forGetter(TaskProgress::isComplete)
        ).apply(instance, TaskProgress::new));
    }

    private Tag progress;
    private boolean complete;

    public TaskProgress(QuestTask<?, ?> task) {
        this.progress = task.storage().createDefault();
        this.complete = false;
    }

    public TaskProgress(Tag progress, boolean complete) {
        this.progress = progress;
        this.complete = complete;
    }

    public <T> void addProgress(QuestTask<T, ?> task, T input) {
        if (complete) return;
        progress = task.test(progress, input);
        if (task.getProgress(progress) >= 1f) {
            complete = true;
        }
    }

    public boolean isComplete() {
        return complete;
    }

    public Tag progress() {
        return progress;
    }

    public TaskProgress copy() {
        return new TaskProgress(progress, complete);
    }
}
