package org.activityinfo.client.importer.page;

import org.activityinfo.client.importer.ont.DataTypeProperty;
import org.activityinfo.client.importer.ont.PropertyPath;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public interface UpdateCommandFactory<T> {

	ScheduledCommand setColumnValue(PropertyPath property, String value);
	
}
