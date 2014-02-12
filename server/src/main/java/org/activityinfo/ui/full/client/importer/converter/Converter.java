package org.activityinfo.ui.full.client.importer.converter;

import javax.annotation.Nonnull;

/**
 * Created by alex on 2/14/14.
 */
public interface Converter {

    /**
     * Converts the non-null {@code value} to the correct type
     */
    @Nonnull
    public Object convertString(@Nonnull String value);
}
