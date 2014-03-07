package org.activityinfo.ui.full.client.importer.converter;

import javax.annotation.Nonnull;

/**
 * Converts string values to a quantity
 */
public class StringToQuantityConverter implements StringConverter<Double> {

    @Nonnull
    @Override
    public Double convert(@Nonnull String value) {
        return Double.parseDouble(value);
    }
}
