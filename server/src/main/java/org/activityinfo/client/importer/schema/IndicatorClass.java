package org.activityinfo.client.importer.schema;

import java.util.Arrays;
import java.util.List;

import org.activityinfo.client.importer.ont.DataTypeProperty;
import org.activityinfo.client.importer.ont.DataTypeProperty.Type;
import org.activityinfo.client.importer.ont.ObjectProperty;
import org.activityinfo.client.importer.ont.OntClass;
import org.activityinfo.client.importer.ont.Property;


public class IndicatorClass extends OntClass {
	
	public static final DataTypeProperty NAME = new DataTypeProperty("name", "Name", Type.STRING);
	public static final DataTypeProperty CATEGORY = new DataTypeProperty("category", "Category", Type.STRING);
	public static final DataTypeProperty DESCRIPTION = new DataTypeProperty("description", "Description", Type.STRING);
	public static final DataTypeProperty UNITS = new DataTypeProperty("units", "Units", Type.STRING);
	public static final ObjectProperty ACTIVITY = new ObjectProperty("activity", "Activity", "Activity");
	
	@Override
	public List<Property> getProperties() {
		return Arrays.asList(NAME, CATEGORY, DESCRIPTION, UNITS, ACTIVITY);
	}
} 
