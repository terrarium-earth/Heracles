package earth.terrarium.hercules.condition;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import earth.terrarium.hercules.Hercules;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Holder;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public interface QuestCondition {
    Codec<QuestCondition> CODEC = Hercules.getConditionRegistryCodec().dispatchStable(QuestCondition::codec, Function.identity());

    boolean isAcquired(Stream<Criterion> criteria);

    default boolean isAcquired(Criterion criterion) {
        return criteria()
                .stream()
                .anyMatch(condition -> condition.map(
                        it -> it.value().equals(criterion),
                        it -> it.isAcquired(Stream.of(criterion))
                ));
    }

    Codec<? extends QuestCondition> codec();

    List<Either<Holder<Criterion>, QuestCondition>> criteria();

    default Stream<Criterion> allCriteria() {
        return criteria().stream().flatMap(condition -> condition.map(criterion -> Stream.of(criterion.value()), QuestCondition::allCriteria));
    }

    static <T extends QuestCondition> MapCodec<T> simple(Function<List<Either<Holder<Criterion>, QuestCondition>>, T> factory) {
        return Codec.either(CriterionCodecs.CODEC, CODEC)
                .listOf()
                .fieldOf("criteria")
                .xmap(factory, T::criteria);
    }
}
