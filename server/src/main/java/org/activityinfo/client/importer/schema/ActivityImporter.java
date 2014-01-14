package org.activityinfo.client.importer.schema;

import java.util.Map;

import org.activityinfo.client.importer.binding.DraftModel;
import org.activityinfo.client.importer.binding.InstanceImporter;
import org.activityinfo.client.importer.binding.InstanceMatch;
import org.activityinfo.client.importer.ont.OntClass;
import org.activityinfo.client.importer.ont.PropertyPath;
import org.activityinfo.shared.command.Command;
import org.activityinfo.shared.command.CreateEntity;
import org.activityinfo.shared.dto.LocationTypeDTO;
import org.activityinfo.shared.dto.UserDatabaseDTO;

import com.google.common.collect.Maps;


public class ActivityImporter implements InstanceImporter {

	private Map<PropertyPath, Object> providedValues;
	private UserDatabaseDTO database;

	public ActivityImporter(UserDatabaseDTO database) {
		this.database = database;
		providedValues = Maps.newHashMap();
		providedValues.put(new PropertyPath(ActivityClass.DATABASE), database);
	}
	
	@Override
	public OntClass resolveOntClass(String range) {
		if(range.equals("Database")) {
			return new DatabaseClass();
		} else if(range.equals("LocationType")) {
			return new LocationTypeClass();
		}
		throw new IllegalArgumentException();
	}

	@Override
	public OntClass getOntClass() {
		return new ActivityClass();
	}

	@Override
	public Map<PropertyPath, Object> getProvidedValues() {
		return providedValues;
	}

	@Override
	public InstanceMatch matchInstance(String ontClass,
			Map<String, Object> properties) {
		
		if(ontClass.equals("LocationType")) {
			for(LocationTypeDTO locationType : database.getCountry().getLocationTypes()) {
				if(locationType.getName().equals(properties.get("name"))) {
					return InstanceMatch.existing(locationType.getId());
				}
			}
		}
		return null;
	}

	@Override
	public Command<?> createCommand(DraftModel draftModel) {
		return new CreateEntity("Activity", draftModel.asLegacyPropertyMap());
	}

}
