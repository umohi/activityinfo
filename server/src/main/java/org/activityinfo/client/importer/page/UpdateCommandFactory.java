package org.activityinfo.client.importer.page;

import org.activityinfo.client.importer.binding.Property;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public interface UpdateCommandFactory<T> {

	ScheduledCommand setColumnValue(Property<T, ?> property, String value);
	
}
