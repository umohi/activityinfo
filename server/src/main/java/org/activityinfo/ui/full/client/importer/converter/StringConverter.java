package org.activityinfo.ui.full.client.importer.converter;

import javax.annotation.Nonnull;

/**
 * Converts an imported value to String
 */
public class StringConverter implements Converter  {

    @Nonnull
    @Override
    public String convertString(@Nonnull String value) {
        return value;
    }
}
