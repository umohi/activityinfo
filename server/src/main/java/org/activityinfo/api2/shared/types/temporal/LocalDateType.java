package org.activityinfo.api2.shared.types.temporal;

import org.activityinfo.api2.shared.types.DataType;

/**
 *
 */
public class LocalDateType extends DataType<LocalDateValue> {

    public static final String TYPE_CLASS_ID = "localDate";

    @Override
    public String getTypeClassId() {
        return TYPE_CLASS_ID;
    }
}
