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

    public static final QuestConditionCodec<AnyOfQuestCondition> CODEC = new QuestConditionCodec<>(
            deserializationContext -> QuestCondition.simpleCodec(deserializationContext, AnyOfQuestCondition::new).codec(),
            QuestCondition.simpleNetworkCodec(AnyOfQuestCondition::new).codec()
    );

    @Override
    public boolean isAcquired(Stream<Criterion> criteria) {
        return criteria.anyMatch(this::isAcquired);
    }

    @Override
    public QuestConditionCodec<AnyOfQuestCondition> codec() {
        return CODEC;
    }
}
