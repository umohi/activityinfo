package org.activityinfo.api2.shared.types;

/**
 *
 */
public class TextValue extends FieldValue {

    public String value;

    public TextValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getTypeClassId() {
        return TextDataType.TYPE_CLASS_ID;
    }

    public static TextValue valueOf(String string) {
        return new TextValue(string);
    }
}
