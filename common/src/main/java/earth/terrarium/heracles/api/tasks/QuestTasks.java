package earth.terrarium.heracles.api.tasks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class QuestTasks {

    private static final Map<ResourceLocation, QuestTaskSerializer<?>> SERIALIZERS = new HashMap<>();

    public static final Codec<QuestTaskSerializer<?>> TYPE_CODEC = ResourceLocation.CODEC.comapFlatMap(QuestTasks::decode, QuestTaskSerializer::id);
    public static final Codec<QuestTask<?, ?>> CODEC = TYPE_CODEC.dispatch(QuestTask::serializer, QuestTaskSerializer::codec);

    private static DataResult<? extends QuestTaskSerializer<?>> decode(ResourceLocation id) {
        return Optional.ofNullable(SERIALIZERS.get(id))
            .map(DataResult::success)
            .orElse(DataResult.error(() -> "No quest task type found with id " + id));
    }

    public static <C extends QuestTask<?, C>, T extends QuestTaskSerializer<C>> void register(T serializer) {
        if (SERIALIZERS.containsKey(serializer.id()))
            throw new RuntimeException("Multiple quest task serializers registered with same id '" + serializer.id() +"'");
        SERIALIZERS.put(serializer.id(), serializer);
    }
}
