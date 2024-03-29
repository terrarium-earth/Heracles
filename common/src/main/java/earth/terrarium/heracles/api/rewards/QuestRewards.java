package earth.terrarium.heracles.api.rewards;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.resourcefullib.common.codecs.maps.DispatchMapCodec;
import earth.terrarium.heracles.api.rewards.defaults.*;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class QuestRewards {

    private static final Map<ResourceLocation, QuestRewardType<?>> TYPES = new HashMap<>();

    public static final Codec<QuestRewardType<?>> TYPE_CODEC = ResourceLocation.CODEC.comapFlatMap(QuestRewards::decode, QuestRewardType::id);
    public static final Codec<Map<String, QuestReward<?>>> CODEC = DispatchMapCodec.of(Codec.STRING, id -> TYPE_CODEC.dispatch(QuestReward::type, type -> type.codec(id)));
    public static final ByteCodec<Map<String, QuestReward<?>>> BYTE_CODEC = ModUtils.toByteCodec(CODEC, "No quest reward data found", "Failed to parse quest reward data");

    private static DataResult<? extends QuestRewardType<?>> decode(ResourceLocation id) {
        return Optional.ofNullable(TYPES.get(id))
            .map(DataResult::success)
            .orElse(DataResult.error(() -> "No quest reward type found with id " + id));
    }

    public static <R extends QuestReward<R>, T extends QuestRewardType<R>> void register(T type) {
        if (TYPES.containsKey(type.id()))
            throw new RuntimeException("Multiple quest reward types registered with same id '" + type.id() + "'");
        TYPES.put(type.id(), type);
    }

    public static QuestRewardType<?> get(ResourceLocation id) {
        return TYPES.get(id);
    }

    public static Map<ResourceLocation, QuestRewardType<?>> types() {
        return TYPES;
    }

    static {
        register(XpQuestReward.TYPE);
        register(ItemReward.TYPE);
        register(LootTableReward.TYPE);
        register(SelectableReward.TYPE);
        register(CommandReward.TYPE);
    }
}
