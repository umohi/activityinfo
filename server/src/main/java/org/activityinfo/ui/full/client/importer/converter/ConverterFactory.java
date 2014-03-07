package org.activityinfo.ui.full.client.importer.converter;

import org.activityinfo.api2.shared.form.FormFieldType;

/**
 * Creates a converter for a specific field type
 */
public class ConverterFactory {

    private ConverterFactory() {
    }

    public static Converter create(FormFieldType from, FormFieldType to) {
        switch (from) {
            case FREE_TEXT:
            case NARRATIVE:
                return createStringConverter(to);
            case QUANTITY:
                return createQuantityConverter(to);
            case REFERENCE:
                throw new IllegalArgumentException("Reference fields are handled elsewhere");
        }
        throw new UnsupportedOperationException("Conversion from " + from.name() + " to " + to.name() + " is not supported.");
    }

    public static Converter createQuantityConverter(FormFieldType to) {
        switch(to) {
            case QUANTITY:
                return FakeConverter.INSTANCE;
            case FREE_TEXT:
            case NARRATIVE:
                return QuantityToStringConverter.INSTANCE;
            case LOCAL_DATE:
            case GEOGRAPHIC_POINT:
            case REFERENCE:
                throw new UnsupportedOperationException("Conversion from QUANTITY to " + to.name() + " is not supported.");
        }
        throw new UnsupportedOperationException(to.name());
    }

    public static Converter createStringConverter(FormFieldType fieldType) {
        switch(fieldType) {
            case QUANTITY:
                return StringToQuantityConverter.INSTANCE;
            case NARRATIVE:
            case FREE_TEXT:
                return FakeConverter.INSTANCE;
            case REFERENCE:
                throw new IllegalArgumentException("Reference fields are handled elsewhere");
            case LOCAL_DATE:
                return StringToDateConverter.INSTANCE;
            case GEOGRAPHIC_POINT:
        }
        throw new UnsupportedOperationException(fieldType.name());
    }
}
