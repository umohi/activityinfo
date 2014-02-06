package org.activityinfo.api.shared.adapter;

import org.activityinfo.api.shared.model.ActivityDTO;
import org.activityinfo.api.shared.model.AttributeGroupDTO;
import org.activityinfo.api.shared.model.EntityDTO;
import org.activityinfo.api2.client.CuidGenerator;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Cuids;
import org.activityinfo.api2.shared.Iri;

import javax.annotation.Nonnull;

/**
 * Provides an adapter between legacy ids, which are either random or sequential 32-bit integers but only
 * guaranteed to be unique within a table, and Collision Resistant Universal Ids (CUIDs) which
 * will serve as the identifiers for all user-created objects.
 */
public class CuidAdapter {

    public static final char SITE_DOMAIN = 's';

    public static final char ACTIVITY_DOMAIN = 'a';

    public static final char LOCATION_DOMAIN = 'g'; // avoid lower case l !

    public static final char LOCATION_TYPE_DOMAIN = 'L'; // avoid lower case l !


    public static final char PARTNER_DOMAIN = 'p';

    public static final char INDICATOR_DOMAIN = 'i';

    public static final char ATTRIBUTE_GROUP_DOMAIN = 'A';


    /**
     * Avoid instance creation.
     */
    private CuidAdapter() {
    }

    public static int getLegacyIdFromCuidIri(@Nonnull Cuid iri) {
        return Integer.parseInt(iri.asString().substring(1), Cuids.RADIX);
    }

    public static final int getLegacyIdFromCuid(String cuid) {
        return Integer.parseInt(cuid.substring(1), Cuids.RADIX);
    }

    public static final Cuid cuid(char domain, int id) {
        return new Cuid(domain + Integer.toString(id, Cuids.RADIX));
    }

    public static final Iri iri(char domain, int id) {
        return cuid(domain, id).asIri();
    }

    public static int getLegacyIdFromCuid(Cuid id) {
        return getLegacyIdFromCuid(id.asString());
    }

    /**
     * @return the {@code FormField} Cuid for the Partner field of a given Activity {@code FormClass}
     */
    public static Cuid partnerField(int activityId) {
        return new Cuid(ACTIVITY_DOMAIN + block(activityId) + "p");
    }

    /**
     * @return the {@code FormField}  Cuid for the Location field of a given Activity {@code FormClass}
     */
    public static Cuid locationField(int activityId) {
        return new Cuid(ACTIVITY_DOMAIN + block(activityId) + "L");
    }

    /**
     * @return the {@code FormClass} Cuid for a given LocationType
     */
    public static Cuid locationFormClass(int locationTypeId) {
        return cuid(LOCATION_TYPE_DOMAIN, locationTypeId);

    }

    /**
     * @return the {@code FormClass} Cuid for a given Activity
     */
    public static Cuid activityFormClass(int activityId) {
        return new Cuid(ACTIVITY_DOMAIN + block(activityId));
    }


    /**
     * @return the {@code FormClass} Cuid for a given Activity
     */
    public static Cuid commentsField(int activityId) {
        return new Cuid(ACTIVITY_DOMAIN + block(activityId) + "C");

    }

    /**
     * @return the {@code FormField} Cuid for the indicator field within a given
     * Activity {@code FormClass}
     */
    public static Cuid indicatorField(int indicatorId) {
        return cuid(INDICATOR_DOMAIN, indicatorId);
    }

    /**
     * @return the {@code FormField} Cuid for the field of a given Activity {@code FormClass} that
     * references the given AttributeGroup FormClass
     */
    public static Cuid attributeGroupField(ActivityDTO activity, AttributeGroupDTO group) {
        return new Cuid(ACTIVITY_DOMAIN + block(activity) + "a" +
                Integer.toString(group.getId(), Cuids.RADIX));
    }

    /**
     * @return the {@code FormClass} Cuid for a given AttributeGroup
     */
    public static Cuid attributeGroupFormClass(AttributeGroupDTO group) {
        return cuid(ATTRIBUTE_GROUP_DOMAIN, group.getId());
    }

    /**
     * @return the {@code FormSection} Cuid for a given indicator category within an
     * Activity {@code FormClass}
     */
    public static Cuid activityFormSection(int id, String name) {
        return new Cuid(ACTIVITY_DOMAIN + block(id) + block(name.hashCode()));
    }

    private static String block(int id) {
        return CuidGenerator.pad(id);
    }

    private static String block(EntityDTO entity) {
        return block(entity.getId());
    }

    private static Cuid cuid(char domain, EntityDTO entityDTO) {
        return cuid(domain, entityDTO.getId());
    }

}
