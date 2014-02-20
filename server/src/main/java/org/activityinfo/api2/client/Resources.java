package org.activityinfo.api2.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.api2.shared.Projection;
import org.activityinfo.api2.shared.form.FormInstance;

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

    public AsyncFunction<FormInstance, Void> persist() {
        return new AsyncFunction<FormInstance, Void>() {
            @Override
            public void apply(FormInstance resource, AsyncCallback<Void> callback) {
                resourceLocator.persist(resource).then(callback);
            }
        };
    }

    public AsyncFunction<InstanceQuery, List<Projection>> query() {
        return new AsyncFunction<InstanceQuery, List<Projection>>() {
            @Override
            public void apply(InstanceQuery instanceQuery, AsyncCallback<List<Projection>> callback) {
                resourceLocator.query(instanceQuery).then(callback);
            }
        };
    }

    public Promise<List<Projection>> query(InstanceQuery query) {
        return resourceLocator.query(query);
    }
}
