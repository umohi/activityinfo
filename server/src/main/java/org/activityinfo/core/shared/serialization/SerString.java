package org.activityinfo.core.shared.serialization;

/**
 * A serialized string of characters
 */
public class SerString extends SerValue {

    private final String value;

    public SerString(String value) {
        this.value = value;
    }

    @Override
    public boolean isString() {
        return true;
    }

    @Override
    public String asString() {
        return value;
    }
}
