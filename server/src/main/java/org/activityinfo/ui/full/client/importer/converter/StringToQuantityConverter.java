package org.activityinfo.ui.full.client.importer.converter;

import org.activityinfo.core.client.formatter.GwtQuantityFormatterFactory;
import org.activityinfo.core.shared.formatter.QuantityFormatter;

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
