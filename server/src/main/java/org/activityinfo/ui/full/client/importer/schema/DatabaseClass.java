package org.activityinfo.ui.full.client.importer.schema;

import org.activityinfo.ui.full.client.importer.ont.DataTypeProperty;
import org.activityinfo.ui.full.client.importer.ont.OntClass;
import org.activityinfo.ui.full.client.importer.ont.Property;

import java.util.Arrays;
import java.util.List;

public class DatabaseClass extends OntClass {

    public static final DataTypeProperty NAME = new DataTypeProperty("name", "label", DataTypeProperty.Type.STRING);

    @Override
    public List<Property> getProperties() {
        return Arrays.asList((Property) NAME);
    }

}
