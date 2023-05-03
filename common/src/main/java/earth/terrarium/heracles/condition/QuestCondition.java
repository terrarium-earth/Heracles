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
    Codec<QuestCondition> CODEC = Heracles.getConditionRegistryCodec().dispatch(QuestCondition::codec, Function.identity());

    boolean isAcquired(Stream<QuestTask> tasks);

    default boolean isAcquired(QuestTask task) {
        return tasks()
                .stream()
                .anyMatch(condition -> condition.map(
                        it -> it.equals(task),
                        it -> it.isAcquired(Stream.of(task))
                ));
    }

    Codec<? extends QuestCondition> codec();

    List<Either<QuestTask, QuestCondition>> tasks();

    default Stream<QuestTask> allTasks() {
        return tasks().stream().flatMap(condition -> condition.map(Stream::of, QuestCondition::allTasks));
    }

    static <T extends QuestCondition> MapCodec<T> simpleCodec(Function<List<Either<QuestTask, QuestCondition>>, T> factory) {
        return Codec.either(QuestTask.CODEC, CODEC)
                .listOf()
                .fieldOf("tasks")
                .xmap(factory, T::tasks);
    }
}
