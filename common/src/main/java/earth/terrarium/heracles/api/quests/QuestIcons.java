package earth.terrarium.heracles.api.quests;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.teamresourceful.bytecodecs.base.ByteCodec;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class QuestIcons {

    private static final Map<ResourceLocation, QuestIconType<?>> TYPES = new HashMap<>();

    public static final Codec<QuestIconType<?>> TYPE_CODEC = ResourceLocation.CODEC.comapFlatMap(QuestIcons::decode, QuestIconType::id);
    public static final Codec<QuestIcon<?>> CODEC = TYPE_CODEC.dispatch(QuestIcon::type, QuestIconType::codec);

    public static final ByteCodec<QuestIcon<?>> BYTE_CODEC = ModUtils.toByteCodec(CODEC, "No quest icon data found", "Failed to parse quest icon data");

    private static DataResult<? extends QuestIconType<?>> decode(ResourceLocation id) {
        return Optional.ofNullable(TYPES.get(id))
            .map(DataResult::success)
            .orElse(DataResult.error(() -> "No quest icon type found with id " + id));
    }

    public static <R extends QuestIcon<R>, T extends QuestIconType<R>> void register(T type) {
        if (TYPES.containsKey(type.id()))
            throw new RuntimeException("Multiple quest icon types registered with same id '" + type.id() + "'");
        TYPES.put(type.id(), type);
    }

    static {
        register(ItemQuestIcon.TYPE);
    }
}
