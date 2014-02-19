package org.activityinfo.ui.full.client.importer.converter;

import javax.annotation.Nonnull;

/**
 * Converts raw imported values from the {@code ImportSource} to the
 * correct field value
 */
public interface Converter {

    /**
     * Converts the non-null {@code value} to the correct type
     */
    @Nonnull
    public Object convertString(@Nonnull String value);
}
