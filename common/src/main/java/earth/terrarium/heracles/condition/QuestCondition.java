package earth.terrarium.heracles.condition;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import earth.terrarium.heracles.Heracles;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.DeserializationContext;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public interface QuestCondition {
    boolean isAcquired(Stream<Criterion> criteria);

    default boolean isAcquired(Criterion criterion) {
        return criteria()
                .stream()
                .anyMatch(condition -> condition.map(
                        it -> it.equals(criterion),
                        it -> it.isAcquired(Stream.of(criterion))
                ));
    }

    QuestConditionCodec<? extends QuestCondition> codec();

    List<Either<Criterion, QuestCondition>> criteria();

    default Stream<Criterion> allCriteria() {
        return criteria().stream().flatMap(condition -> condition.map(Stream::of, QuestCondition::allCriteria));
    }

    static Codec<QuestCondition> dispatchCodec(DeserializationContext deserializationContext) {
        return Heracles.getConditionRegistryCodec().dispatch(QuestCondition::codec, codec -> codec.dataCodec().apply(deserializationContext));
    }

    static Codec<QuestCondition> dispatchNetworkCodec() {
        return Heracles.getConditionRegistryCodec().dispatch(QuestCondition::codec, QuestConditionCodec::networkCodec);
    }

    static <T extends QuestCondition> MapCodec<T> simpleCodec(DeserializationContext deserializationContext, Function<List<Either<Criterion, QuestCondition>>, T> factory) {
        return Codec.either(Criteria.criterionCodec(deserializationContext), dispatchCodec(deserializationContext))
                .listOf()
                .fieldOf("criteria")
                .xmap(factory, T::criteria);
    }

    static <T extends QuestCondition> MapCodec<T> simpleNetworkCodec(Function<List<Either<Criterion, QuestCondition>>, T> factory) {
        return Codec.either(Criteria.networkCodec(), dispatchNetworkCodec())
                .listOf()
                .fieldOf("criteria")
                .xmap(factory, T::criteria);
    }

    record QuestConditionCodec<T extends QuestCondition>(Function<DeserializationContext, Codec<T>> dataCodec, Codec<T> networkCodec) {}
}
