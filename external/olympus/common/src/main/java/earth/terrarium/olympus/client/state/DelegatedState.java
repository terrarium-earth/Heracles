package earth.terrarium.olympus.client.state;

import earth.terrarium.olympus.client.utils.State;

public abstract class DelegatedState<T> implements State<T> {

    protected final State<T> delegate;

    public DelegatedState(State<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void set(T value) {
        this.delegate.set(value);
    }

    @Override
    public T get() {
        return this.delegate.get();
    }
}
