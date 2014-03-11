package org.activityinfo.ui.full.client.pageView;

import com.google.gwt.user.client.ui.IsWidget;
import org.activityinfo.api2.shared.form.FormInstance;

/**
 * A UI component that renders a page for a given class of {@code FormInstance}
 */
public interface InstancePageView extends IsWidget {

    void show(FormInstance instance);
}
