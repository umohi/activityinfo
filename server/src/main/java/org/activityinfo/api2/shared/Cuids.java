package org.activityinfo.api2.shared;

/**
 * Functions that operator on Collision-resistant Universal ids (CUID)
 */
public class Cuids {

    public static final String SCHEME = "cuid";

    public static final String IRI_PREFIX = SCHEME + ":";

    public static final int RADIX = Character.MAX_RADIX;

    /**
     * Constructs an IRI from a given CUID
     * @param cuid a collision resistant universal id (Cuid)
     * @return an IRI with the cuid: scheme
     */
    public static Iri toIri(String cuid) {
        return new Iri(IRI_PREFIX + cuid);
    }

    /**
     * Constructs an IRI from a legacy id.
     *
     * @param cuidDomain a single char which provides a namespace for the ID
     * @param id the original numeric id
     * @return an IRI with the cuid: scheme
     */
    public static Iri toIri(char cuidDomain, int id) {
        return new Iri(IRI_PREFIX + cuidDomain + Integer.toString(id, RADIX));

    }
}
