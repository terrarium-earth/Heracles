package earth.terrarium.heracles.common.utils;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.*;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public final class XorMapCodec<F, S> extends MapCodec<Either<F, S>> {

    private final MapCodec<F> first;
    private final MapCodec<S> second;

    public XorMapCodec(MapCodec<F> codec, MapCodec<S> codec2) {
        this.first = codec;
        this.second = codec2;
    }

    public static <F, S> MapCodec<Either<F, S>> create(MapCodec<F> first, MapCodec<S> second) {
        return new XorMapCodec<>(first, second);
    }

    @Override
    public <T> DataResult<Either<F, S>> decode(DynamicOps<T> ops, MapLike<T> input) {
        DataResult<Either<F, S>> left = this.first.decode(ops, input).map(Either::left);
        DataResult<Either<F, S>> right = this.second.decode(ops, input).map(Either::right);
        Optional<Either<F, S>> leftResult = left.result();
        Optional<Either<F, S>> rightResult = right.result();
        if (leftResult.isPresent() && rightResult.isPresent()) {
            return DataResult.error(() -> "Both alternatives read successfully, can not pick the correct one; first: " + leftResult.get() + " second: " + rightResult.get(), leftResult.get());
        } else {
            return leftResult.isPresent() ? left : right;
        }
    }

    @Override
    public <T> RecordBuilder<T> encode(Either<F, S> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
        return input.map(object2 -> this.first.encode(object2, ops, prefix), object2 -> this.second.encode(object2, ops, prefix));
    }

    @Override
    public <T> Stream<T> keys(DynamicOps<T> ops) {
        return Stream.concat(first.keys(ops), second.keys(ops)).distinct();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            XorMapCodec<?, ?> xorCodec = (XorMapCodec<?, ?>) object;
            return Objects.equals(this.first, xorCodec.first) && Objects.equals(this.second, xorCodec.second);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(this.first, this.second);
    }

    public String toString() {
        return "XorMapCodec[" + this.first + ", " + this.second + "]";
    }
}
