package org.activityinfo.ui.full.client.importer.converter;

import javax.annotation.Nonnull;

/**
 * Converts an imported value to String
 */
public class StringToStringConverter implements StringConverter<String>  {

    @Nonnull
    @Override
    public String convert(@Nonnull String value) {
        return value;
    }
}
