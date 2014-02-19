package org.activityinfo.api2.client.promises;

import com.google.gwt.core.client.testing.StubScheduler;
import org.activityinfo.api2.client.Promise;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.activityinfo.api2.client.PromiseMatchers.assertResolves;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by alex on 2/18/14.
 */
public class NonBlockingSynchronousMapFunctionTest {


    @Test
    public void testApply() throws Exception {


        Action<Integer> worker = new Action<Integer>() {
            @Override
            public void execute(Integer input) {
                System.out.println("input: " + input);
            }
        };

        assertResolves(execute(Arrays.asList(1), worker));
        assertResolves(execute(Collections.<Integer>emptyList(), worker));
        assertResolves(execute(Arrays.asList(1, 2, 3, 4), worker));
        assertResolves(execute(Arrays.asList(3), worker));
    }

    @Test
    public void testExceptionDuringApply() throws Exception {


        Action<Integer> worker = new Action<Integer>() {
            @Override
            public void execute(Integer input) {
                System.out.println("input: " + input);
                if(input == 3) {
                    throw new IllegalArgumentException();
                }
            }
        };

        Promise<Void> promise = execute(Arrays.asList(1, 2, 3), worker);

        assertThat(promise.getState(), equalTo(Promise.State.REJECTED));

    }


    private Promise<Void> execute(List<Integer> input, Action<Integer> worker) {
        StubScheduler scheduler = new StubScheduler();

        NonBlockingSynchronousMapFunction<Integer> mapFunction = new NonBlockingSynchronousMapFunction<>(scheduler, worker);
        Promise<Void> promise = Promise.promise(input, mapFunction);

        while(scheduler.executeCommands()) {  }

        return promise;
    }
}
