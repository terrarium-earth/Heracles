package earth.terrarium.hercules.condition;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Holder;

import java.util.List;
import java.util.stream.Stream;

public record AllOfQuestCondition(List<Either<Holder<Criterion>, QuestCondition>> criteria) implements QuestCondition {
    public static final MapCodec<AllOfQuestCondition> MAP_CODEC = QuestCondition.simple(AllOfQuestCondition::new);

    @Override
    public boolean isAcquired(Stream<Criterion> criteria) {
        return criteria.allMatch(this::isAcquired);
    }

    @Override
    public Codec<? extends QuestCondition> codec() {
        return MAP_CODEC.codec();
    }
}
