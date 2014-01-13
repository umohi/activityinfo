package org.activityinfo.client.importer.schema;

import java.util.List;

import org.activityinfo.client.importer.binding.PropertyBinder;
import org.activityinfo.client.importer.ont.ModelBinder;
import org.activityinfo.client.importer.ont.Property;
import org.activityinfo.shared.command.Command;
import org.activityinfo.shared.dto.ActivityDTO;
import org.activityinfo.shared.dto.IndicatorDTO;
import org.activityinfo.shared.dto.UserDatabaseDTO;

import com.google.common.collect.Lists;


public class IndicatorBinder implements ModelBinder<IndicatorDTO> {

	private final UserDatabaseDTO database;
	private final ActivityClass activityBinder;
	private final List<Property> properties = Lists.newArrayList();

	public IndicatorBinder(UserDatabaseDTO database) {
		super();
		this.database = database;
		this.activityBinder = new ActivityClass(database);
	}
	
	@Override
	public List<Property> getProperties() {

		this.columnBinders.add(Property.create("Indicator Name", new PropertyBinder<IndicatorDTO, String>() {
			@Override
			public boolean update(IndicatorDTO model, String value) {
				model.setName(value);
				return true;
			}

			@Override
			public String get(IndicatorDTO model) {
				return model.getName();
			}
		}));

		this.columnBinders.add(Property.create("Indicator Category", new PropertyBinder<IndicatorDTO, String>() {
			@Override
			public boolean update(IndicatorDTO model, String value) {
				model.setCategory(value);	
				return true;
			}

			@Override
			public String get(IndicatorDTO model) {
				return model.getCategory();
			}
		}));


		this.columnBinders.add(Property.create("Indicator Units", new PropertyBinder<IndicatorDTO, String>() {
			@Override
			public boolean update(IndicatorDTO model, String value) {
				model.setUnits(value);
				return true;
			}

			@Override
			public String get(IndicatorDTO model) {
				return model.getUnits();
			}
		}));


		this.columnBinders.add(Property.create("Indicator Description", new PropertyBinder<IndicatorDTO, String>() {
			@Override
			public boolean update(IndicatorDTO model, String value) {
				model.setDescription(value);
				return true;
			}

			@Override
			public String get(IndicatorDTO model) {
				return model.getDescription();
			}
		}));
	
		return this.columnBinders;
	}


	@Override
	public IndicatorDTO newModel() {
		IndicatorDTO indicator = new IndicatorDTO();
		
	}

	@Override
	public Command<?> createCommand(IndicatorDTO model) {
		// TODO Auto-generated method stub
		return null;
	}

} 
