package org.activityinfo.api2.client;


import com.google.common.base.Function;

import javax.annotation.Nullable;

public abstract class Action<T> implements Function<T, Void> {

    @Nullable
    @Override
    public final Void apply(@Nullable T input) {
        execute(input);
        return null;
    }

    public abstract void execute(T input);
}
