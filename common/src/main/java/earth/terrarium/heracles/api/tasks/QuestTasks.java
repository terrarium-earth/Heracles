package earth.terrarium.heracles.api.tasks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.teamresourceful.resourcefullib.common.codecs.maps.DispatchMapCodec;
import earth.terrarium.heracles.api.tasks.defaults.*;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class QuestTasks {

    private static final Map<ResourceLocation, QuestTaskType<?>> TYPES = new HashMap<>();

    public static final Codec<QuestTaskType<?>> TYPE_CODEC = ResourceLocation.CODEC.comapFlatMap(QuestTasks::decode, QuestTaskType::id);
    public static final Codec<Map<String, QuestTask<?, ?, ?>>> CODEC = DispatchMapCodec.of(Codec.STRING, id -> TYPE_CODEC.dispatch(QuestTask::type, type -> type.codec(id)));

    private static DataResult<? extends QuestTaskType<?>> decode(ResourceLocation id) {
        return Optional.ofNullable(TYPES.get(id))
            .map(DataResult::success)
            .orElse(DataResult.error(() -> "No quest task type found with id " + id));
    }

    public static <C extends QuestTask<?, ?, C>, T extends QuestTaskType<C>> void register(T type) {
        if (TYPES.containsKey(type.id()))
            throw new RuntimeException("Multiple quest task types registered with same id '" + type.id() + "'");
        TYPES.put(type.id(), type);
    }

    public static Map<ResourceLocation, QuestTaskType<?>> types() {
        return TYPES;
    }

    public static QuestTaskType<?> get(ResourceLocation id) {
        return TYPES.get(id);
    }

    static {
        register(CompositeTask.TYPE);
        register(KillEntityQuestTask.TYPE);
        register(GatherItemTask.TYPE);
        register(BiomeTask.TYPE);
        register(StructureTask.TYPE);
        register(ChangedDimensionTask.TYPE);
        register(AdvancementTask.TYPE);
        register(RecipeTask.TYPE);
        register(ItemInteractTask.TYPE);
        register(ItemUseTask.TYPE);
        register(BlockInteractTask.TYPE);
        register(CheckTask.TYPE);
        register(DummyTask.TYPE);
    }
}
