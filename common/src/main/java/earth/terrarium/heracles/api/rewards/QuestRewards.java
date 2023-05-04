package earth.terrarium.heracles.api.rewards;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class QuestRewards {

    private static final Map<ResourceLocation, QuestRewardSerializer<?>> SERIALIZERS = new HashMap<>();

    public static final Codec<QuestRewardSerializer<?>> TYPE_CODEC = ResourceLocation.CODEC.comapFlatMap(QuestRewards::decode, QuestRewardSerializer::id);
    public static final Codec<QuestReward<?>> CODEC = TYPE_CODEC.dispatch(QuestReward::serializer, QuestRewardSerializer::codec);

    private static DataResult<? extends QuestRewardSerializer<?>> decode(ResourceLocation id) {
        return Optional.ofNullable(SERIALIZERS.get(id))
            .map(DataResult::success)
            .orElse(DataResult.error(() -> "No quest reward type found with id " + id));
    }

    public static <R extends QuestReward<R>, T extends QuestRewardSerializer<R>> void register(T serializer) {
        if (SERIALIZERS.containsKey(serializer.id()))
            throw new RuntimeException("Multiple quest reward serializers registered with same id '" + serializer.id() +"'");
        SERIALIZERS.put(serializer.id(), serializer);
    }
}
