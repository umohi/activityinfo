package org.activityinfo.client.importer.schema;

public interface WarningCollector {
	void warn(String message);
	void error(String message);
}
