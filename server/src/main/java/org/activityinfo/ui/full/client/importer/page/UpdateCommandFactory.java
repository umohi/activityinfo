package org.activityinfo.ui.full.client.importer.page;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import org.activityinfo.api2.shared.form.tree.FieldPath;

public interface UpdateCommandFactory<T> {

    ScheduledCommand setColumnValue(FieldPath property, String value);

}
