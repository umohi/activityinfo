package org.activityinfo.api2.client.promises;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Static utility methods that create {@code OnlineReducers}
 *
 */
public class Reducers {

    /**
     *
     * @return Creates a reducer which ignores its input and returns {@coid Void}
     */
    public static <T> ReduceFunction<T, Void> nullReducer() {
        return new ReduceFunction<T, Void>() {
            @Override
            public void init(Void initialValue) { }

            @Override
            public void update(T input) { }

            @Override
            public Void reduce() {
                return null;
            }
        };
    }

    /**
     *
     * @return An {@code OnlineReducer} which counts the number of inputs, null or non-null
     */
    public static ReduceFunction<Object, Integer> count() {
        return new ReduceFunction<Object, Integer>() {
            private int count = 0;

            @Override
            public void init(Integer initialValue) {
                count = initialValue == null ? 0 : initialValue;
            }

            @Override
            public void update(Object input) {
                count++;
            }

            @Override
            public Integer reduce() {
                return count;
            }
        };
    }

    /**
     *
     * @return an {@code OnlineReducer} which creates an {@code ArrayList} from the input items
     */
    public static <T> ReduceFunction<T, List<T>> makeList() {
        return new ReduceFunction<T, List<T>>() {

            private List<T> list;

            @Override
            public void init(List<T> initialValue) {
                list = initialValue == null ? Lists.<T>newArrayList() : initialValue;
            }

            @Override
            public void update(T input) {
                list.add(input);
            }

            @Override
            public List<T> reduce() {
                return list;
            }
        };
    }

    public static ReduceFunction<Boolean, Integer> countTrue() {
        return new ReduceFunction<Boolean, Integer>() {

            private int count = 0;

            @Override
            public void init(Integer initialValue) {
                count = initialValue == null ? 0 : initialValue;
            }

            @Override
            public void update(Boolean input) {
                if(input) {
                    count++;
                }
            }

            @Override
            public Integer reduce() {
                return count;
            }
        };
    }
}
