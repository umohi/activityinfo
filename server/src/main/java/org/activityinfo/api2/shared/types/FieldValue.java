package org.activityinfo.api2.shared.types;

import com.google.gson.JsonElement;

/**
 * The base class for values that can be stored as
 * as part of an instance
 */
public abstract class FieldValue {

    public abstract String getTypeClassId();

    /**
     * @return a serialized form of the value
     */
    public abstract JsonElement serialize();
}
