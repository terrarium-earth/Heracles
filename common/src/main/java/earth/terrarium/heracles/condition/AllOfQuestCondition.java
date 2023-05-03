package earth.terrarium.heracles.condition;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.DeserializationContext;

import java.util.List;
import java.util.stream.Stream;

public record AllOfQuestCondition(List<Either<Criterion, QuestCondition>> criteria) implements QuestCondition {
    public static final String KEY = "all_of";

    public static final QuestConditionCodec<AllOfQuestCondition> CODEC = new QuestConditionCodec<>(
            deserializationContext -> QuestCondition.simpleCodec(deserializationContext, AllOfQuestCondition::new).codec(),
            QuestCondition.simpleNetworkCodec(AllOfQuestCondition::new).codec()
    );

    @Override
    public boolean isAcquired(Stream<Criterion> criteria) {
        return criteria.allMatch(this::isAcquired);
    }

    @Override
    public QuestConditionCodec<AllOfQuestCondition> codec() {
        return CODEC;
    }
}
