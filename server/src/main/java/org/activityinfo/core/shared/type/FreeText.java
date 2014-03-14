package org.activityinfo.core.shared.type;

import org.activityinfo.core.shared.serialization.SerString;
import org.activityinfo.core.shared.serialization.SerValue;

/**
 * Single line of text
 */
public class FreeText implements FieldValue {

    public static final String TYPE_CLASS_ID = "text";

    private final String value;

    public FreeText(String value) {
        this.value = value;
    }


    @Override
    public String getTypeClassId() {
        return TYPE_CLASS_ID;
    }

    @Override
    public SerValue serialize() {
        return new SerString(value);
    }

    public static class Parser implements FieldValueParser {

        @Override
        public FreeText parse(SerValue value) {
            return new FreeText(value.asString());
        }
    }
}
