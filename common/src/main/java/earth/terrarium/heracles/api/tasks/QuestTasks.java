package earth.terrarium.heracles.api.tasks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import earth.terrarium.heracles.api.tasks.defaults.AdvancementTask;
import earth.terrarium.heracles.api.tasks.defaults.EnterDimensionTask;
import earth.terrarium.heracles.api.tasks.defaults.FindBiomeTask;
import earth.terrarium.heracles.api.tasks.defaults.FindStructureTask;
import earth.terrarium.heracles.api.tasks.defaults.ItemQuestTask;
import earth.terrarium.heracles.api.tasks.defaults.KillEntityQuestTask;
import earth.terrarium.heracles.api.tasks.defaults.RecipeTask;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class QuestTasks {

    private static final Map<ResourceLocation, QuestTaskType<?>> TYPES = new HashMap<>();

    public static final Codec<QuestTaskType<?>> TYPE_CODEC = ResourceLocation.CODEC.comapFlatMap(QuestTasks::decode, QuestTaskType::id);
    public static final Codec<QuestTask<?, ?>> CODEC = TYPE_CODEC.dispatch(QuestTask::type, QuestTaskType::codec);

    private static DataResult<? extends QuestTaskType<?>> decode(ResourceLocation id) {
        return Optional.ofNullable(TYPES.get(id))
            .map(DataResult::success)
            .orElse(DataResult.error(() -> "No quest task type found with id " + id));
    }

    public static <C extends QuestTask<?, C>, T extends QuestTaskType<C>> void register(T type) {
        if (TYPES.containsKey(type.id()))
            throw new RuntimeException("Multiple quest task types registered with same id '" + type.id() + "'");
        TYPES.put(type.id(), type);
    }

    static {
        register(KillEntityQuestTask.TYPE);
        register(ItemQuestTask.TYPE);
        register(FindBiomeTask.TYPE);
        register(FindStructureTask.TYPE);
        register(EnterDimensionTask.TYPE);
        register(AdvancementTask.TYPE);
        register(RecipeTask.TYPE);
    }
}
