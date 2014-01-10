package org.activityinfo.client.importer.schema;

import java.util.List;
import java.util.Map;

import org.activityinfo.client.i18n.I18N;
import org.activityinfo.client.importer.binding.Binder;
import org.activityinfo.client.importer.binding.Property;
import org.activityinfo.client.importer.binding.PropertyBinder;
import org.activityinfo.client.local.command.handler.KeyGenerator;
import org.activityinfo.shared.dto.ActivityDTO;
import org.activityinfo.shared.dto.UserDatabaseDTO;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ActivityBinder implements Binder<ActivityDTO> {
	
	private final KeyGenerator keyGenerator = new KeyGenerator();
	private final UserDatabaseDTO database;
	private List<Property<ActivityDTO, ?>> columnBinders = Lists.newArrayList();
	
	public ActivityBinder(UserDatabaseDTO database) {
		super();
		this.database = database;
		this.columnBinders.add(Property.create(I18N.CONSTANTS.name(), new PropertyBinder<ActivityDTO, String>() {
			@Override
			public boolean update(ActivityDTO model, String value) {
				model.setName(value);
				return true;
			}
		}));
			
		this.columnBinders.add(Property.create(I18N.CONSTANTS.category(), new PropertyBinder<ActivityDTO, String>() {
			@Override
			public boolean update(ActivityDTO model, String value) {
				model.setCategory(value);	
				return true;
			}
		}));
		
		this.columnBinders.add(Property.create(I18N.CONSTANTS.locationType(), new PropertyBinder<ActivityDTO, String>() {

			@Override
			public boolean update(ActivityDTO model, String value) {
				return true;
			}
		}));
		
		Map<String, Integer> frequencyMap = Maps.newHashMap();
		frequencyMap.put(I18N.CONSTANTS.reportOnce(), ActivityDTO.REPORT_ONCE);
		frequencyMap.put(I18N.CONSTANTS.monthly(), ActivityDTO.REPORT_MONTHLY);
		
		this.columnBinders.add(Property.<ActivityDTO, Integer>create(I18N.CONSTANTS.reportingFrequency(), 
			frequencyMap,
			new PropertyBinder<ActivityDTO, Integer>() {
	
				@Override
				public boolean update(ActivityDTO model, Integer value) {
					model.setReportingFrequency(value);
					return true;
				}
			}));
	}

	@Override
	public List<Property<ActivityDTO, ?>> getProperties() {
		return columnBinders;
	}

	@Override
	public ActivityDTO createNew() {
		ActivityDTO activity = new ActivityDTO();
		activity.setId(keyGenerator.generateInt());
		activity.setDatabase(database);
		return activity;
	}
}
