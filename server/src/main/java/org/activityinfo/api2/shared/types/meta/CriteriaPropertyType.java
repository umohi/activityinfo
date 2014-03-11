package org.activityinfo.api2.shared.types.meta;

import org.activityinfo.api2.shared.types.DataType;

/**
 * A Property containing an instance criteria expression
 */
public class CriteriaPropertyType extends DataType<CriteriaValue> {

    public static final String TYPE_CLASS_ID = "criteria";

    @Override
    public String getTypeClassId() {
        return TYPE_CLASS_ID;
    }
}
