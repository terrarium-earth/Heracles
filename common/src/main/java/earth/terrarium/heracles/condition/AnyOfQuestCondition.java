package earth.terrarium.heracles.condition;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.DeserializationContext;

import java.util.List;
import java.util.stream.Stream;

public record AnyOfQuestCondition(List<Either<Criterion, QuestCondition>> criteria) implements QuestCondition {
    public static final String KEY = "any_of";

    @Override
    public boolean isAcquired(Stream<Criterion> criteria) {
        return criteria.anyMatch(this::isAcquired);
    }

    @Override
    public Codec<? extends QuestCondition> codec(DeserializationContext deserializationContext) {
        return simpleCodec(deserializationContext);
    }

    public static Codec<AnyOfQuestCondition> simpleCodec(DeserializationContext deserializationContext) {
        return mapCodec(deserializationContext).codec();
    }

    public static MapCodec<AnyOfQuestCondition> mapCodec(DeserializationContext deserializationContext) {
        return QuestCondition.simpleCodec(deserializationContext, AnyOfQuestCondition::new);
    }
}
