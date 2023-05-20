package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.QuestTasks;
import earth.terrarium.heracles.api.tasks.storage.TaskStorage;
import earth.terrarium.heracles.api.tasks.storage.defaults.CompositeTaskStorage;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;

import java.util.Map;

@SuppressWarnings("unchecked")
public final class CompositeTask implements QuestTask<Object, ListTag, CompositeTask> {

    public static final QuestTaskType<CompositeTask> TYPE = new Type();

    private final String id;
    private final int amount;
    private final Map<String, QuestTask<?, ?, ?>> tasks;
    private final CompositeTaskStorage storage;

    public CompositeTask(String id, int amount, Map<String, QuestTask<?, ?, ?>> tasks) {
        this.id = id;
        this.amount = amount;
        this.tasks = tasks;

        storage = new CompositeTaskStorage(tasks.values().stream().map(QuestTask::storage).toList());
    }

    @Override
    public String id() {
        return id;
    }

    public int amount() {
        return amount;
    }

    public Map<String, QuestTask<?, ?, ?>> tasks() {
        return tasks;
    }

    @Override
    public ListTag test(QuestTaskType<?> type, ListTag progress, Object input) {
        int i = 0;
        for (QuestTask<?, ?, ?> task : tasks.values()) {
            if (!task.isCompatibleWith(type)) continue;

            progress.set(i, ((QuestTask<Object, Tag, ?>) task).test(type, progress.get(i), input));
            i++;
        }

        return progress;
    }

    @Override
    public float getProgress(ListTag progress) {
        return Mth.clamp(getCompletedTasks(progress) / amount, 0, 1);
    }

    public float getCompletedTasks(ListTag progress) {
        float totalProgress = 0;

        int i = 0;
        for (QuestTask<?, ?, ?> task : tasks.values()) {
            totalProgress += ((QuestTask<?, Tag, ?>) task).getProgress(progress.get(i));
            i++;
        }

        return totalProgress;
    }

    @Override
    public TaskStorage<?, ListTag> storage() {
        return storage;
    }

    @Override
    public boolean isCompatibleWith(QuestTaskType<?> type) {
        return tasks.values().stream().anyMatch(task -> task.isCompatibleWith(type));
    }

    @Override
    public QuestTaskType<CompositeTask> type() {
        return TYPE;
    }

    private static class Type implements QuestTaskType<CompositeTask> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "composite");
        }

        @Override
        public Codec<CompositeTask> codec(String id) {
            return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                ExtraCodecs.POSITIVE_INT.fieldOf("amount").forGetter(CompositeTask::amount),
                QuestTasks.CODEC.fieldOf("tasks").forGetter(CompositeTask::tasks)
            ).apply(instance, CompositeTask::new));
        }
    }
}
