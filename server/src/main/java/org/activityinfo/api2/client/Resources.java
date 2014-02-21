package org.activityinfo.api2.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.api2.client.promises.AsyncConsumer;
import org.activityinfo.api2.shared.Projection;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.function.Consumer;
import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Functional forms of the {@code ResourceLocator} methods
 */
public class Resources {

    public final ResourceLocator resourceLocator;

    public Resources(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
    }

    public Function<FormInstance, Promise<Void>> persist() {
        return new Function<FormInstance, Promise<Void>>() {
            @Override
            public Promise<Void> apply(FormInstance formInstance) {
                return resourceLocator.persist(formInstance);
            }
        };
    }

    public Function<InstanceQuery, Promise<List<Projection>>> query() {
        return new Function<InstanceQuery, Promise<List<Projection>>>() {

            @Override
            public Promise<List<Projection>> apply(InstanceQuery instanceQuery) {
                return resourceLocator.query(instanceQuery);
            }
        };
    }

    public Promise<List<Projection>> query(InstanceQuery query) {
        return resourceLocator.query(query);
    }
}
