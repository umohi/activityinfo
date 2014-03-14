package org.activityinfo.core.shared.type;

import com.google.common.base.Preconditions;
import org.activityinfo.core.shared.serialization.SerString;
import org.activityinfo.core.shared.serialization.SerValue;

/**
 * A narrative text which may include multiple paragraphs (and eventually some sort of markup)
 */
public class Narrative implements FieldValue {

    public static final String TYPE_CLASS_ID = "quantity";

    public final String value;

    public Narrative(String value) {
        Preconditions.checkNotNull(value);
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
        public Narrative parse(SerValue value) {
            return new Narrative(value.asString());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Narrative narrative = (Narrative) o;

        if (!value.equals(narrative.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
