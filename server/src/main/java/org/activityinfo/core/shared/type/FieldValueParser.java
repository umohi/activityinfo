package org.activityinfo.core.shared.type;

import org.activityinfo.core.shared.serialization.SerValue;

/**
 * Parses a serialized value into a specific type of field value
 */
public interface FieldValueParser {

    public FieldValue parse(SerValue value);
}
