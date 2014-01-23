package org.activityinfo.ui.full.client.importer.schema;

import org.activityinfo.ui.full.client.importer.ont.DataTypeProperty;
import org.activityinfo.ui.full.client.importer.ont.DataTypeProperty.Type;
import org.activityinfo.ui.full.client.importer.ont.ObjectProperty;
import org.activityinfo.ui.full.client.importer.ont.OntClass;
import org.activityinfo.ui.full.client.importer.ont.Property;

import java.util.Arrays;
import java.util.List;

public class ActivityClass extends OntClass {

    public static final DataTypeProperty NAME = new DataTypeProperty("name", "Name", Type.STRING);
    public static final DataTypeProperty CATEGORY = new DataTypeProperty("category", "Category", Type.STRING);
    public static final ObjectProperty LOCATION_TYPE = new ObjectProperty("locationType", "Location Type", "LocationType");
    public static final ObjectProperty DATABASE = new ObjectProperty("database", "Database", "Database");

    public ActivityClass() {
        super();
    }

    @Override
    public List<Property> getProperties() {
        return Arrays.asList(NAME, CATEGORY, LOCATION_TYPE, DATABASE);
    }
}
