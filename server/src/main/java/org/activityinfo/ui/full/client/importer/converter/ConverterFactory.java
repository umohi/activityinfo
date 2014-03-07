package org.activityinfo.ui.full.client.importer.converter;

import org.activityinfo.api2.shared.form.FormFieldType;

/**
 * Creates a converter for a specific field type
 */
public class ConverterFactory {

    public static StringConverter create(FormFieldType fieldType) {
        switch(fieldType) {
            case QUANTITY:
                return new StringToQuantityConverter();
            case NARRATIVE:
            case FREE_TEXT:
                return new StringToStringConverter();
            case REFERENCE:
                throw new IllegalArgumentException("Reference fields are handled elsewhere");
            case LOCAL_DATE:
                return new StringToDateConverter();
            case GEOGRAPHIC_POINT:
        }
        throw new UnsupportedOperationException(fieldType.name());
    }
}
