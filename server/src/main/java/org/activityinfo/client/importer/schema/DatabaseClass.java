package org.activityinfo.client.importer.schema;

import java.util.Arrays;
import java.util.List;

import org.activityinfo.client.importer.ont.DataTypeProperty;
import org.activityinfo.client.importer.ont.OntClass;
import org.activityinfo.client.importer.ont.Property;

public class DatabaseClass extends OntClass {

	public static final DataTypeProperty NAME = new DataTypeProperty("name", "label", DataTypeProperty.Type.STRING);
	
	@Override
	public List<Property> getProperties() {
		return Arrays.asList((Property)NAME);
	}
	
}
