package org.activityinfo.api2.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;

/**
 * Hamcrest matchers for promises
 */
public class PromiseMatchers {

    public static <T> Matcher<Promise<T>> resolution(final Matcher<T> matcher) {
        return new TypeSafeMatcher<Promise<T>>() {

            private T resolution = null;

            @Override
            public boolean matchesSafely(Promise<T> item) {

                item.then(new AsyncCallback<T>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        throw new AssertionError(throwable);
                    }

                    @Override
                    public void onSuccess(T t) {
                        resolution = t;
                    }
                });

                return matcher.matches(resolution);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("resolves to value ");
                matcher.describeTo(description);
            }
        };
    }
}
