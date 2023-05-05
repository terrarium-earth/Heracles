package earth.terrarium.heracles.api.tasks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import earth.terrarium.heracles.api.tasks.defaults.ItemQuestTask;
import earth.terrarium.heracles.api.tasks.defaults.KillEntityQuestTask;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class QuestTasks {

    private static final Map<ResourceLocation, QuestTaskType<?>> SERIALIZERS = new HashMap<>();

    public static final Codec<QuestTaskType<?>> TYPE_CODEC = ResourceLocation.CODEC.comapFlatMap(QuestTasks::decode, QuestTaskType::id);
    public static final Codec<QuestTask<?, ?>> CODEC = TYPE_CODEC.dispatch(QuestTask::type, QuestTaskType::codec);

    private static DataResult<? extends QuestTaskType<?>> decode(ResourceLocation id) {
        return Optional.ofNullable(SERIALIZERS.get(id))
            .map(DataResult::success)
            .orElse(DataResult.error(() -> "No quest task type found with id " + id));
    }

    public static <C extends QuestTask<?, C>, T extends QuestTaskType<C>> void register(T serializer) {
        if (SERIALIZERS.containsKey(serializer.id()))
            throw new RuntimeException("Multiple quest task serializers registered with same id '" + serializer.id() + "'");
        SERIALIZERS.put(serializer.id(), serializer);
    }

    static {
        register(KillEntityQuestTask.TYPE);
        register(ItemQuestTask.TYPE);
    }
}
