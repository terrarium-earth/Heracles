package earth.terrarium.heracles.common.utils;

import java.util.Optional;
import java.util.function.BiFunction;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class BiOptional {

    public static <T, A, B> Optional<T> map(Optional<A> a, Optional<B> b, BiFunction<A, B, T> mapper) {
        if (a.isEmpty() || b.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(mapper.apply(a.get(), b.get()));
    }

    public static <T, A, B> Optional<T> flatMap(Optional<A> a, Optional<B> b, BiFunction<A, B, Optional<T>> mapper) {
        if (a.isEmpty() || b.isEmpty()) {
            return Optional.empty();
        }
        return mapper.apply(a.get(), b.get());
    }
}
