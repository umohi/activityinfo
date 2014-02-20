package org.activityinfo.api2.client.promises;

/**
 * Online
 */
public interface ReduceFunction<T, F> {

    void init(F initialValue);

    /**
     * Updates the state of the reducer with the next element in the list
     * @param input
     */
    void update(T input);

    /**
     *
     * @return the current state of the reducer
     */
    F reduce();

}
