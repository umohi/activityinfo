package org.activityinfo.api2.shared.types.spatial;

import org.activityinfo.api2.shared.types.DataType;

/**
 * Geometric Point
 */
public class GeographicPointType extends DataType {

    public static final String TYPE_CLASS_ID = "geographicPoint";

    @Override
    public String getTypeClassId() {
        return TYPE_CLASS_ID;
    }
}
