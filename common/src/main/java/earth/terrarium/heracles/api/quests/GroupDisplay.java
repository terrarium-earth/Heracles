package earth.terrarium.heracles.api.quests;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import earth.terrarium.heracles.common.utils.ModUtils;
import org.joml.Vector2i;

public record GroupDisplay(String id, Vector2i position) {

    public static final ByteCodec<GroupDisplay> BYTE_CODEC = ObjectByteCodec.create(
        ByteCodec.STRING.fieldOf(GroupDisplay::id),
        ModUtils.VECTOR2I_BYTE_CODEC.fieldOf(GroupDisplay::position),
        GroupDisplay::new
    );

    public static Codec<GroupDisplay> codec(String id) {
        return RecordCodecBuilder.create(instance -> instance.group(
            RecordCodecBuilder.point(id),
            ModUtils.VECTOR2I.fieldOf("position").orElse(new Vector2i()).forGetter(GroupDisplay::position)
        ).apply(instance, GroupDisplay::new));
    }

    public static GroupDisplay createDefault() {
        return new GroupDisplay("Main", new Vector2i());
    }
}
