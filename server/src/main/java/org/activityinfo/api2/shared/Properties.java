package org.activityinfo.api2.shared;

/**
 * Created by alexander on 3/6/14.
 */
public class Properties {

    /**
     * Application-defined property that provides a human-readable name for
     * a given form instance. <strong>Should not be used as a form field id, only as a super property</strong>
     */
    public static final Cuid LABEL_PROPERTY = new Cuid("_label");

    /**
     * Application-defined property that provides an extended human-readable description
     * for a given form instance. <strong>Should not be used as a form field id, only as a super property</strong>
     */
    public static final Cuid DESCRIPTION_PROPERTY = new Cuid("_description");

    /**
     * Application-defined property that provides the class ids of an instance.
     * <strong>Should not be used as a form field id, only as a super property</strong>
     */
    public static final Cuid CLASS_PROPERTY = new Cuid("_classOf");


    public static final Cuid FORM_CLASS = new Cuid("_class");
}
