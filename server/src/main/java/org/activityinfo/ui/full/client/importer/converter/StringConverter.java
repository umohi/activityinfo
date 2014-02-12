package org.activityinfo.ui.full.client.importer.converter;

import javax.annotation.Nonnull;

/**
 * Performs no conversion
 */
public class StringConverter implements Converter  {

    @Nonnull
    @Override
    public String convertString(@Nonnull String value) {
        return value;
    }
}
