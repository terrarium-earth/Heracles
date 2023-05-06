package earth.terrarium.heracles.api.rewards;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class QuestRewards {

    private static final Map<ResourceLocation, QuestRewardType<?>> SERIALIZERS = new HashMap<>();

    public static final Codec<QuestRewardType<?>> TYPE_CODEC = ResourceLocation.CODEC.comapFlatMap(QuestRewards::decode, QuestRewardType::id);
    public static final Codec<QuestReward<?>> CODEC = TYPE_CODEC.dispatch(QuestReward::type, QuestRewardType::codec);

    private static DataResult<? extends QuestRewardType<?>> decode(ResourceLocation id) {
        return Optional.ofNullable(SERIALIZERS.get(id))
            .map(DataResult::success)
            .orElse(DataResult.error(() -> "No quest reward type found with id " + id));
    }

    public static <R extends QuestReward<R>, T extends QuestRewardType<R>> void register(T serializer) {
        if (SERIALIZERS.containsKey(serializer.id()))
            throw new RuntimeException("Multiple quest reward serializers registered with same id '" + serializer.id() + "'");
        SERIALIZERS.put(serializer.id(), serializer);
    }
}
