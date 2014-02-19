package org.activityinfo.ui.full.client.importer.converter;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import com.google.common.base.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Converts string values to a quantity
 */
public class QuantityConverter implements Converter {

    @Nonnull
    @Override
    public Double convertString(@Nonnull String value) {
        return Double.parseDouble(value);
    }
}
