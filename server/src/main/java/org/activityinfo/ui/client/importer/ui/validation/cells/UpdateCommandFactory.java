package org.activityinfo.ui.client.importer.ui.validation.cells;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import org.activityinfo.core.shared.form.tree.FieldPath;

public interface UpdateCommandFactory<T> {

    ScheduledCommand setColumnValue(FieldPath property, String value);

}
