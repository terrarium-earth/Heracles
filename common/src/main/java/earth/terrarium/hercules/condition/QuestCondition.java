package earth.terrarium.hercules.condition;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import earth.terrarium.hercules.Hercules;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public interface QuestCondition {
    Codec<QuestCondition> DIRECT_CODEC = Hercules.getConditionRegistryCodec().dispatchStable(QuestCondition::codec, Function.identity());

    Codec<Holder<QuestCondition>> CODEC = RegistryFileCodec.create(Hercules.QUEST_CONDITION_REGISTRY_KEY, DIRECT_CODEC, false);

    boolean isAcquired(Stream<Criterion> criteria);

    default boolean isAcquired(Criterion criterion) {
        return criteria()
                .stream()
                .anyMatch(condition -> condition.map(
                        it -> it.value().equals(criterion),
                        it -> it.value().isAcquired(Stream.of(criterion))
                ));
    }

    Codec<? extends QuestCondition> codec();

    List<Either<Holder<Criterion>, Holder<QuestCondition>>> criteria();

    default Stream<Holder<Criterion>> allCriteria() {
        return criteria().stream().flatMap(condition -> condition.map(Stream::of, questCondition -> questCondition.value().allCriteria()));
    }

    static <T extends QuestCondition> MapCodec<T> simple(Function<List<Either<Holder<Criterion>, Holder<QuestCondition>>>, T> factory) {
        return Codec.either(CriterionCodecs.CODEC, CODEC)
                .listOf()
                .fieldOf("criteria")
                .xmap(factory, T::criteria);
    }
}
