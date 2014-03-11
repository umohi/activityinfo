package org.activityinfo.api2.shared.types.temporal;

import org.activityinfo.api2.shared.types.DataType;

/**
 * Property which takes only a year and a month
 */
public class MonthType extends DataType<MonthValue> {

    public static final String TYPE_CLASS_ID = "month";

    @Override
    public String getTypeClassId() {
        return null;
    }
}
