package org.activityinfo.api.shared.adapter;

import org.activityinfo.api.client.KeyGenerator;
import org.activityinfo.api.shared.model.ActivityDTO;
import org.activityinfo.api.shared.model.AttributeGroupDTO;
import org.activityinfo.api.shared.model.EntityDTO;
import org.activityinfo.api.shared.model.UserDatabaseDTO;
import org.activityinfo.api2.client.CuidGenerator;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Cuids;
import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.form.FormInstance;

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

    public static final char PARTNER_FORM_CLASS_DOMAIN = 'P';

    public static final char INDICATOR_DOMAIN = 'i';

    public static final char ATTRIBUTE_GROUP_DOMAIN = 'A';

    public static final char ATTRIBUTE_DOMAIN = 't';

    public static final char DATABASE_DOMAIN = 'd';

    public static final char ADMIN_LEVEL_DOMAIN = 'E';

    public static final char ADMIN_ENTITY_DOMAIN = 'e';

    public static final char PROJECT_CLASS_DOMAIN = 'R';

    public static final char PROJECT_DOMAIN = 'r';

    public static final char ACTIVITY_CATEGORY_DOMAIN = 'g';

    public static final int NAME_FIELD = 1;
    public static final int ADMIN_PARENT_FIELD = 2;
    public static final int CODE_FIELD = 3;
    public static final int AXE_FIELD = 4;
    public static final int GEOMETRY_FIELD = 4;
    public static final int ADMIN_FIELD = 4;
    public static final int PARTNER_FIELD = 5;
    public static final int PROJECT_FIELD = 6;
    public static final int DATE_FIELD = 7;
    public static final int FULL_NAME_FIELD = 8;
    public static final int LOCATION_FIELD = 9;

    /**
     * Avoid instance creation.
     */
    private CuidAdapter() {
    }

    // todo yuriyz -> alex : please check it
    public static Cuid newSectionField() {
        return CuidAdapter.cuid('x', new KeyGenerator().generateInt());
    }

    // todo yuriyz -> alex : please check it
    public static Cuid newFormField() {
        return CuidAdapter.cuid('x', new KeyGenerator().generateInt());
    }

    // todo yuriyz -> alex : please check it, right now used to add new form instance to form field
    public static Cuid newFormInstance() {
        return attributeId(new KeyGenerator().generateInt());
    }

    public static Cuid getFormInstanceLabelCuid(FormInstance formInstance) {
        return CuidAdapter.field(formInstance.getClassId(), NAME_FIELD);
    }

    public static int getLegacyIdFromCuidIri(@Nonnull Iri iri) {
        String iriAsString = iri.asString();
        if (iriAsString.startsWith(Cuids.IRI_PREFIX)) {
            iriAsString = iriAsString.substring(Cuids.IRI_PREFIX.length());
        }
        return Integer.parseInt(iriAsString.substring(1), Cuids.RADIX);
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
        return field(activityFormClass(activityId), PARTNER_FIELD);
    }

    public static Cuid projectField(int activityId) {
        return field(activityFormClass(activityId), PROJECT_FIELD);
    }

    public static Cuid partnerInstanceId(int partnerId) {
        return cuid(PARTNER_DOMAIN, partnerId);
    }

    public static Cuid adminField(Cuid locationTypeFormClass, int adminLevelId) {
        return new Cuid(locationTypeFormClass.asString() + block(ADMIN_FIELD) + block(adminLevelId));
    }

    /**
     * @return the {@code FormField}  Cuid for the Location field of a given Activity {@code FormClass}
     */
    public static Cuid locationField(int activityId) {
        return field(activityFormClass(activityId), LOCATION_FIELD);
    }

    /**
     * @return the {@code FormClass} Cuid for a given LocationType
     */
    public static Cuid locationFormClass(int locationTypeId) {
        return cuid(LOCATION_TYPE_DOMAIN, locationTypeId);
    }

    public static Cuid locationInstanceId(int locationId) {
        return cuid(LOCATION_DOMAIN, locationId);
    }

    public static Cuid adminLevelFormClass(int adminLevelId) {
        return cuid(ADMIN_LEVEL_DOMAIN, adminLevelId);
    }


    public static Cuid adminEntityInstanceId(int adminEntityId) {
        return cuid(ADMIN_ENTITY_DOMAIN, adminEntityId);

    }

    /**
     * Generates a CUID for a FormField in a given previously-built-in FormClass using
     * the FormClass's CUID and a field index.
     * @param classId
     * @param fieldIndex
     * @return
     */
    public static Cuid field(Cuid classId, int fieldIndex) {
        return new Cuid(classId.asString() + block(fieldIndex));
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

    public static Cuid attributeField(int attributeId) {
        return cuid(ATTRIBUTE_DOMAIN, attributeId);
    }

    public static Cuid siteField(int siteId) {
        return cuid(INDICATOR_DOMAIN, siteId);
    }

    /**
     * @return the {@code FormField} Cuid for the field of a given Activity {@code FormClass} that
     * references the given AttributeGroup FormClass
     */
    public static Cuid attributeGroupField(ActivityDTO activity, AttributeGroupDTO group) {
        return new Cuid(ACTIVITY_DOMAIN + block(activity) + "a" +
                Integer.toString(group.getId(), Cuids.RADIX));
    }


    public static Cuid activityCategoryFolderId(UserDatabaseDTO db, String category) {
        return new Cuid(ACTIVITY_CATEGORY_DOMAIN + block(db.getId()) + block(category.hashCode()));
    }

    /**
     * @return the {@code FormClass} Cuid for a given AttributeGroup
     */
    public static Cuid attributeGroupFormClass(AttributeGroupDTO group) {
        return attributeGroupFormClass(group.getId());
    }

    public static Cuid attributeGroupFormClass(int attributeGroupId) {
        return cuid(ATTRIBUTE_GROUP_DOMAIN, attributeGroupId);
    }

    public static Cuid attributeId(int attributeId) {
        return cuid(ATTRIBUTE_DOMAIN, attributeId);
    }

    /**
     * @param databaseId the id of the user database
     * @return the {@code FormClass} Cuid for a given database's list of partners.
     */
    public static Cuid partnerFormClass(int databaseId) {
        return cuid(PARTNER_FORM_CLASS_DOMAIN, databaseId);
    }

    /**
     * @param databaseId the id of the user database
     * @return the {@code FormClass} Cuid for a given database's list of projects.
     */
    public static Cuid projectFormClass(int databaseId) {
        return cuid(PROJECT_CLASS_DOMAIN, databaseId);
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

    public static Cuid databaseId(UserDatabaseDTO database) {
        return cuid(DATABASE_DOMAIN, database);
    }
}
