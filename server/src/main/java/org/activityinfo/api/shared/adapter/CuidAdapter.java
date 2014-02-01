package org.activityinfo.api.shared.adapter;

import org.activityinfo.api2.shared.Cuids;
import org.activityinfo.api2.shared.Iri;

/**
 * Provides an adapter between legacy ids, which are either random or sequential 32-bit integers but only
 * guaranteed to be unique within a table, and Collision Resistant Universal Ids (CUIDs) which
 * will serve as the identifiers for all user-created objects.
 */
public class CuidAdapter {

    public static final char SITE_DOMAIN = 's';

    public static final char ACTIVITY_DOMAIN = 'a';

    public static final char LOCATION_DOMAIN = 'g'; // avoid lower case l !

    public static final char PARTNER_DOMAIN = 'p';

    public static final char INDICATOR_DOMAIN = 'i';

    public static final int getLegacyIdFromCuid(String cuid) {
        return Integer.parseInt(cuid.substring(1), Cuids.RADIX);
    }

    public static final String cuid(char domain, int id) {
        return domain + Integer.toString(id, Cuids.RADIX);
    }

    public static final Iri iri(char domain, int id) {
        return new Iri(cuid(domain, id));
    }

}
