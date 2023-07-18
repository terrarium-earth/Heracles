package earth.terrarium.heracles.api.tasks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.codecs.KeyDispatchCodec;
import com.teamresourceful.resourcefullib.common.codecs.maps.DispatchMapCodec;
import earth.terrarium.heracles.api.tasks.defaults.*;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class QuestTasks {

    private static final Map<ResourceLocation, QuestTaskType<?>> TYPES = new HashMap<>();

    public static final Codec<QuestTaskType<?>> TYPE_CODEC = ResourceLocation.CODEC.comapFlatMap(QuestTasks::decode, QuestTaskType::id);
    public static final Codec<Map<String, QuestTask<?, ?, ?>>> CODEC = DispatchMapCodec.of(Codec.STRING, id -> of("type", TYPE_CODEC,
        quest -> DataResult.success(quest.type()),
        type -> DataResult.success(type.codec(id))
    ).codec());

    public static KeyDispatchCodec<QuestTaskType<?>, QuestTask<?, ?, ?>> of(final String typeKey, final Codec<QuestTaskType<?>> keyCodec, final Function<? super QuestTask<?, ?, ?>, ? extends DataResult<? extends QuestTaskType<?>>> type, final Function<? super QuestTaskType<?>, ? extends DataResult<? extends Codec<? extends QuestTask<?, ?, ?>>>> codec) {
        return KeyDispatchCodec.unsafe(typeKey, keyCodec, type, codec, v -> getCodec(type, codec, v));
    }

    @SuppressWarnings("unchecked")
    private static <K, V> DataResult<? extends Encoder<V>> getCodec(final Function<? super V, ? extends DataResult<? extends K>> type, final Function<? super K, ? extends DataResult<? extends Encoder<? extends V>>> encoder, final V input) {
        return type.apply(input).<Encoder<? extends V>>flatMap(k -> encoder.apply(k).map(Function.identity())).map(c -> ((Encoder<V>) c));
    }

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
        register(XpTask.TYPE);
        register(EntityInteractTask.TYPE);
        register(LocationTask.TYPE);
        register(StatTask.TYPE);
    }
}
