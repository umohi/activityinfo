package org.activityinfo.api2.client;

import com.google.gwt.view.client.ProvidesKey;
import org.activityinfo.api2.shared.Projection;

/**
 * Provides keys for Projections
 */
public class ProjectionKeyProvider implements ProvidesKey<Projection> {
    @Override
    public String getKey(Projection item) {
        return item.getRootInstanceId().asString();
    }
}
