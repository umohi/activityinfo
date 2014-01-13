package org.activityinfo.client.importer.schema;

import java.util.List;
import java.util.Map;

import org.activityinfo.client.importer.ont.DataTypeProperty;
import org.activityinfo.shared.dto.ActivityDTO;
import org.activityinfo.shared.dto.UserDatabaseDTO;

public class ActivityResolver {
	
	private UserDatabaseDTO database;
	
	private List<ActivityDTO> newActivities;
	
	private ActivityClass binder;
	
	public int resolve(Map<DataTypeProperty<ActivityDTO, ?>, ?> propertyValues) {
		
		
	}
	

}
