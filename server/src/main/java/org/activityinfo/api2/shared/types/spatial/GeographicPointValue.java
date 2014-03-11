package org.activityinfo.api2.shared.types.spatial;

import org.activityinfo.api2.shared.types.FieldValue;

/**
 * Created by alex on 3/10/14.
 */
public class GeographicPointValue extends FieldValue {


    @Override
    public String getTypeClassId() {
        return GeographicPointType.TYPE_CLASS_ID;
    }
}
