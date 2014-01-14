package org.activityinfo.client.importer.schema;

import java.util.Arrays;
import java.util.List;

import org.activityinfo.client.importer.ont.DataTypeProperty;
import org.activityinfo.client.importer.ont.OntClass;
import org.activityinfo.client.importer.ont.DataTypeProperty.Type;
import org.activityinfo.client.importer.ont.Property;

public class LocationTypeClass extends OntClass {

	public static DataTypeProperty NAME = new DataTypeProperty("name", "Name", Type.STRING);

	@Override
	public List<Property> getProperties() {
		return Arrays.asList((Property)NAME);
	}
}
