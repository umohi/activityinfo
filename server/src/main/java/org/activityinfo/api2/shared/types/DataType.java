package org.activityinfo.api2.shared.types;

import com.google.gson.JsonElement;

/**
 * Defines the range and structure of a property and FormField
 */
public abstract class DataType<V extends FieldValue> {

    public abstract String getTypeClassId();

}
