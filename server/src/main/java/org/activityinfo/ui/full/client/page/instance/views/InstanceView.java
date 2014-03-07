package org.activityinfo.ui.full.client.page.instance.views;

import com.google.gwt.user.client.ui.IsWidget;
import org.activityinfo.api2.shared.form.FormInstance;

/**
 *
 */
public interface InstanceView extends IsWidget {

    void show(FormInstance instance);
}
