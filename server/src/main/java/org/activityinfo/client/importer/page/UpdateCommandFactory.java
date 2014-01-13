package org.activityinfo.client.importer.page;

import org.activityinfo.client.importer.ont.DataTypeProperty;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public interface UpdateCommandFactory<T> {

	ScheduledCommand setColumnValue(DataTypeProperty<T, ?> property, String value);
	
}
