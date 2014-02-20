package org.activityinfo.glambda.monad;

/**
 * A Monad which wraps a value together with a state object
 */
public class State<S, T> implements Monad<T> {

    private S state;
    private T value;

    public State(S state, T value) {
        this.state = state;
        this.value = value;
    }

    public State(T value) {
        this.value = value;
    }

    public S getState() {
        return state;
    }

    @Override
    public T unwrap() {
        return value;
    }
}
