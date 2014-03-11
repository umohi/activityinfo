package org.activityinfo.api2.shared.types;

/**
 * Property which takes an ARGB color
 */
public class ArgbColorType extends DataType<ArgbValue> {

    public static final String TYPE_CLASS_ID = "argb";

    @Override
    public String getTypeClassId() {
        return TYPE_CLASS_ID;
    }
}
