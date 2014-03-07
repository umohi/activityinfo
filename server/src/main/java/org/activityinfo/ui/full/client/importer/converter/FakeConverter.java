package org.activityinfo.ui.full.client.importer.converter;

import javax.annotation.Nonnull;

/**
 * Converts an imported value to String
 */
public enum FakeConverter implements Converter  {
    INSTANCE;

    @Nonnull
    @Override
    public Object convert(@Nonnull Object value) {
        return value;
    }
}
