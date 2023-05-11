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

import java.util.List;

@SuppressWarnings("unchecked")
public final class CompositeTask implements QuestTask<Object, ListTag, CompositeTask> {

    public static final QuestTaskType<CompositeTask> TYPE = new Type();

    private final String id;
    private final int amount;
    private final List<QuestTask<?, ?, ?>> tasks;
    private final CompositeTaskStorage storage;


    public CompositeTask(String id, int amount, List<QuestTask<?, ?, ?>> tasks) {
        this.id = id;
        this.amount = amount;
        this.tasks = tasks;

        storage = new CompositeTaskStorage(tasks.stream().map(QuestTask::storage).toList());
    }

    @Override
    public String id() {
        return id;
    }

    public int amount() {
        return amount;
    }

    public List<QuestTask<?, ?, ?>> tasks() {
        return tasks;
    }

    @Override
    public ListTag test(ListTag progress, Object input) {
        for (int i = 0; i < tasks.size(); i++) {
            progress.set(i, ((QuestTask<Object, Tag, ?>) tasks.get(i)).test(progress.get(i), input));
        }

        return progress;
    }

    @Override
    public float getProgress(ListTag progress) {
        float totalProgress = 0;

        for (int i = 0; i < tasks.size(); i++) {
            totalProgress += ((QuestTask<?, Tag, ?>) tasks.get(i)).getProgress(progress.get(i));
        }

        return Mth.clamp(totalProgress / amount, 0, 1);
    }

    @Override
    public TaskStorage<?, ListTag> storage() {
        return storage;
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
        public Codec<CompositeTask> codec() {
            return RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("id").forGetter(CompositeTask::id),
                    ExtraCodecs.POSITIVE_INT.fieldOf("amount").forGetter(CompositeTask::amount),
                    QuestTasks.CODEC.listOf().fieldOf("tasks").forGetter(CompositeTask::tasks)
            ).apply(instance, CompositeTask::new));
        }
    }
}
