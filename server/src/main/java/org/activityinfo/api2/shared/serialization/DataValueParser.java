package org.activityinfo.api2.shared.serialization;

import com.google.gson.JsonElement;
import org.activityinfo.api2.shared.types.FieldValue;

/**
 * Converts values back and forth between a json-like structure.
 */
public interface DataValueParser<V extends FieldValue> {


    V deserialize(JsonElement element);

}
