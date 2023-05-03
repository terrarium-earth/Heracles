package earth.terrarium.heracles.condition;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.Criterion;

import java.util.List;
import java.util.stream.Stream;

public record AnyOfQuestCondition(List<Either<QuestTask, QuestCondition>> tasks) implements QuestCondition {
    public static final String KEY = "any_of";

    public static final MapCodec<AnyOfQuestCondition> MAP_CODEC = QuestCondition.simpleCodec(AnyOfQuestCondition::new);

    @Override
    public boolean isAcquired(Stream<QuestTask> tasks) {
        return tasks.anyMatch(this::isAcquired);
    }

    @Override
    public Codec<AnyOfQuestCondition> codec() {
        return MAP_CODEC.codec();
    }
}
