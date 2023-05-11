package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.QuestTasks;
import earth.terrarium.heracles.api.tasks.storage.TaskStorage;
import earth.terrarium.heracles.api.tasks.storage.defaults.BooleanTaskStorage;
import earth.terrarium.heracles.api.tasks.storage.defaults.CompositeTaskStorage;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Objects;

public final class OrTask implements QuestTask<Object, OrTask> {

    public static final QuestTaskType<OrTask> TYPE = new Type();

    private final String id;
    private final List<QuestTask<?, ?>> tasks;
    private final CompositeTaskStorage storage;


    public OrTask(String id, List<QuestTask<?, ?>> tasks) {
        this.id = id;
        this.tasks = tasks;

        storage = new CompositeTaskStorage(tasks.stream().map(QuestTask::storage).toList());
    }

    @Override
    public String id() {
        return id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Tag test(Tag progress, Object input) {
        ListTag listTag = (ListTag) progress;

        for (int i = 0; i < tasks.size(); i++) {
            listTag.set(i, ((QuestTask<Object, ?>) tasks.get(i)).test(listTag.get(i), input));
        }

        return listTag;
    }

    @Override
    public float getProgress(Tag progress) {
        ListTag listTag = (ListTag) progress;

        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getProgress(listTag.get(i)) >= 1) {
                return 1;
            }
        }

        return 0;
    }

    @Override
    public TaskStorage<?> storage() {
        return storage;
    }

    @Override
    public QuestTaskType<OrTask> type() {
        return null;
    }

    public List<QuestTask<?, ?>> getTasks() {
        return tasks;
    }

    private static class Type implements QuestTaskType<OrTask> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "or");
        }

        @Override
        public Codec<OrTask> codec() {
            return RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("id").forGetter(OrTask::id),
                    QuestTasks.CODEC.listOf().fieldOf("tasks").forGetter(OrTask::getTasks)
            ).apply(instance, OrTask::new));
        }
    }
}
