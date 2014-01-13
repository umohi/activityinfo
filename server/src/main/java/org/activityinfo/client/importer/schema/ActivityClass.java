package org.activityinfo.client.importer.schema;

import java.util.List;
import java.util.Map;

import org.activityinfo.client.i18n.I18N;
import org.activityinfo.client.importer.binding.PropertyBinder;
import org.activityinfo.client.importer.ont.DataTypeProperty;
import org.activityinfo.client.importer.ont.ModelBinder;
import org.activityinfo.client.importer.ont.ObjectProperty;
import org.activityinfo.client.importer.ont.OntClass;
import org.activityinfo.client.local.command.handler.KeyGenerator;
import org.activityinfo.shared.command.Command;
import org.activityinfo.shared.command.CreateEntity;
import org.activityinfo.shared.dto.ActivityDTO;
import org.activityinfo.shared.dto.LocationTypeDTO;
import org.activityinfo.shared.dto.UserDatabaseDTO;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ActivityClass extends OntClass {
	
	private DataTypeProperty<ActivityDTO, String> name;
	private DataTypeProperty<ActivityDTO, String> category;
	private ObjectProperty<ActivityDTO, LocationTypeDTO> locationType;
	private DataTypeProperty<ActivityDTO, Integer> reportingFrequency;
	
	public ActivityClass(UserDatabaseDTO database) {
		super();
		this.database = database;
		name = DataTypeProperty.create(I18N.CONSTANTS.name(), new PropertyBinder<ActivityDTO, String>() {
			@Override
			public boolean update(ActivityDTO model, String value) {
				model.setName(value);
				return true;
			}

			@Override
			public String get(ActivityDTO model) {
				return model.getName();
			}
		});
		this.columnBinders.add(name);
			
		category = DataTypeProperty.create(I18N.CONSTANTS.category(), new PropertyBinder<ActivityDTO, String>() {
			@Override
			public boolean update(ActivityDTO model, String value) {
				model.setCategory(value);	
				return true;
			}

			@Override
			public String get(ActivityDTO model) {
				return model.getCategory();
			}
		});
		this.columnBinders.add(category);
		
		Map<String, LocationTypeDTO> locationTypeMap = Maps.newHashMap();
		for(LocationTypeDTO locationType : database.getCountry().getLocationTypes()) {
			locationTypeMap.put(locationType.getName(), locationType);
		}
		
		locationType = DataTypeProperty.<ActivityDTO, LocationTypeDTO>create(I18N.CONSTANTS.locationType(), 
			locationTypeMap,
			new PropertyBinder<ActivityDTO, LocationTypeDTO>() {
	
				@Override
				public boolean update(ActivityDTO model, LocationTypeDTO value) {
					model.setLocationTypeId(value.getId());
					return true;
				}

				@Override
				public LocationTypeDTO get(ActivityDTO model) {
					return model.getLocationType();
				}
			});
		this.columnBinders.add(locationType);
		
		Map<String, Integer> frequencyMap = Maps.newHashMap();
		frequencyMap.put(I18N.CONSTANTS.reportOnce(), ActivityDTO.REPORT_ONCE);
		frequencyMap.put(I18N.CONSTANTS.monthly(), ActivityDTO.REPORT_MONTHLY);
		
		reportingFrequency = DataTypeProperty.<ActivityDTO, Integer>create(I18N.CONSTANTS.reportingFrequency(), 
			frequencyMap,
			new PropertyBinder<ActivityDTO, Integer>() {
	
				@Override
				public boolean update(ActivityDTO model, Integer value) {
					model.setReportingFrequency(value);
					return true;
				}

				@Override
				public Integer get(ActivityDTO model) {
					return model.getReportingFrequency();
				}
			});
		this.columnBinders.add(reportingFrequency);
	}

	@Override
	public List<DataTypeProperty<ActivityDTO, ?>> getProperties() {
		return columnBinders;
	}
	
	

	public DataTypeProperty<ActivityDTO, String> getName() {
		return name;
	}

	public void setName(DataTypeProperty<ActivityDTO, String> name) {
		this.name = name;
	}

	public DataTypeProperty<ActivityDTO, String> getCategory() {
		return category;
	}

	public void setCategory(DataTypeProperty<ActivityDTO, String> category) {
		this.category = category;
	}

	public DataTypeProperty<ActivityDTO, LocationTypeDTO> getLocationType() {
		return locationType;
	}

	public void setLocationType(DataTypeProperty<ActivityDTO, LocationTypeDTO> locationType) {
		this.locationType = locationType;
	}

	public DataTypeProperty<ActivityDTO, Integer> getReportingFrequency() {
		return reportingFrequency;
	}

	public void setReportingFrequency(
			DataTypeProperty<ActivityDTO, Integer> reportingFrequency) {
		this.reportingFrequency = reportingFrequency;
	}

	@Override
	public ActivityDTO newModel() {
		ActivityDTO activity = new ActivityDTO();
		activity.setId(keyGenerator.generateInt());
		activity.setDatabase(database);
		return activity;
	}

	@Override
	public Command<?> createCommand(ActivityDTO model) {
		CreateEntity command = new CreateEntity(model);
		command.getProperties().put("databaseId", database.getId());
		return command;
	}
}
