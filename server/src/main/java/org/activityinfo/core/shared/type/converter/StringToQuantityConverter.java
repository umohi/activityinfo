package org.activityinfo.core.shared.type.converter;

import org.activityinfo.core.client.type.formatter.GwtQuantityFormatterFactory;
import org.activityinfo.core.shared.type.formatter.QuantityFormatter;

import javax.annotation.Nonnull;

/**
 * Converts string values to a quantity
 */
public class StringToQuantityConverter implements StringConverter<Double> {

    public final static StringToQuantityConverter INSTANCE = new StringToQuantityConverter();

    private final static QuantityFormatter FORMATTER = new GwtQuantityFormatterFactory().create();

    private StringToQuantityConverter() {
    }

    @Nonnull
    @Override
    public Double convert(@Nonnull String value) {
        return FORMATTER.parse(value);
    }
}
