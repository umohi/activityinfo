package org.activityinfo.api2.shared;

import javax.annotation.Nonnull;

/**
 * Collision-Resistant Unique ID.
 *
 */
public final class Cuid {

    @Nonnull
    private final String value;

    public Cuid(String value) {
        this.value = value;
    }

    public String asString() {
        return value;
    }

    public Iri asIri() {
        return new Iri(Cuids.IRI_PREFIX + value);
    }

    public char getDomain() {
        return value.charAt(0);
    }
}
