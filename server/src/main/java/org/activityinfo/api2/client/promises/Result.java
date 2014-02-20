package org.activityinfo.api2.client.promises;

import com.google.common.base.Function;

import javax.annotation.Nullable;

/**
 * Monadic value which retains the input of a
 * calculation together with it's result
 */
public class Result<T, F> {
    private T input;
    private F result;

    public Result(T input, F result) {
        this.input = input;
        this.result = result;
    }

    public static <T,F> Function<Function<T,F>, Function<T, Result<T,F>>> lift() {
        return new Function<Function<T, F>, Function<T, Result<T, F>>>() {
            @Override
            public Function<T, Result<T, F>> apply(final Function<T, F> function) {
                return new Function<T, Result<T, F>>() {
                    @Override
                    public Result<T, F> apply(@Nullable T input) {
                        return new Result<>(input, function.apply(input));
                    }
                };
            }
        };
    }
}
