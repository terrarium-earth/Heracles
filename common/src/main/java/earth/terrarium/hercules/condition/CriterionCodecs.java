package earth.terrarium.hercules.condition;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import earth.terrarium.hercules.Hercules;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;

public class CriterionCodecs {
    public static final Codec<Criterion> DIRECT_CODEC = CodecExtras.passthrough(Criterion::serializeToJson, json -> Criterion.criterionFromJson(json.getAsJsonObject(), new DeserializationContext()));
    public static final Codec<Holder<Criterion>> CODEC = RegistryFileCodec.create(Hercules.CRITERION_REGISTRY_KEY, DIRECT_CODEC);
}
