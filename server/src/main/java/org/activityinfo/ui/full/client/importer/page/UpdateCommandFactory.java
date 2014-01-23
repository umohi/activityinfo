package org.activityinfo.ui.full.client.importer.page;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import org.activityinfo.ui.full.client.importer.ont.PropertyPath;

public interface UpdateCommandFactory<T> {

    ScheduledCommand setColumnValue(PropertyPath property, String value);

}
