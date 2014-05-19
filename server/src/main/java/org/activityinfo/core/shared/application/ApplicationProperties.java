package org.activityinfo.core.shared.application;

import org.activityinfo.core.shared.Cuid;

/**
 * Defines Application-level properties.
 * <strong>Should not be used as a form field id, only as a super property</strong>
 */
public class ApplicationProperties {

    /**
     * Application-defined property that provides a human-readable name for
     * a given form instance.
     */
    public static final Cuid LABEL_PROPERTY = new Cuid("_label");

    public static final Cuid PARENT_PROPERTY = new Cuid("_parent");

    /**
     * Application-defined property that provides an extended human-readable description
     * for a given form instance.
     */
    public static final Cuid DESCRIPTION_PROPERTY = new Cuid("_description");

    /**
     * Application-defined property that provides the class ids of an instance.
     */
    public static final Cuid CLASS_PROPERTY = new Cuid("_classOf");


    public static final Cuid HIERARCHIAL = new Cuid("_multiLevel");

    public static final Cuid COUNTRY_CLASS = new Cuid("_country");

    public static final Cuid COUNTRY_NAME_FIELD = new Cuid("_country_name");

    public static final Cuid GEOREF_PROPERTY = new Cuid("_georef");
}
