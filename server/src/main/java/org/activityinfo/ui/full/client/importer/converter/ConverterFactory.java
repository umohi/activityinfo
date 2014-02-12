package org.activityinfo.ui.full.client.importer.converter;

import org.activityinfo.api2.shared.form.FormFieldType;

/**
 * Creates a converter for a specific field type
 */
public class ConverterFactory {

    public static Converter create(FormFieldType fieldType) {
        switch(fieldType) {
            case QUANTITY:
                return new QuantityConverter();
            case NARRATIVE:
            case FREE_TEXT:
                return new StringConverter();
            case REFERENCE:
                throw new IllegalArgumentException("Reference fields are handled elsewhere");
            case LOCAL_DATE:
                return new LocalDateConverter();
            case GEOGRAPHIC_POINT:
        }
        throw new UnsupportedOperationException(fieldType.name());
    }
}
