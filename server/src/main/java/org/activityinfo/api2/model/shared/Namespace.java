package org.activityinfo.api2.model.shared;


/**
 * Namespaces for core Namespace classes and legacy resources
 */
public class Namespace {

    public static final String NS = "http://www.activityinfo.org/resources/core#";

    public static final String LEGACY_NS = "http://www.activityinfo.org/resources/";
    public static final String RDFS_LABEL = "http://www.w3.org/2000/01/rdf-schema#label";


    public static final Iri siteForm(int activityId) {
        return new Iri(LEGACY_NS + "Activity/" + activityId + "/SiteForm");
    }

    public static final Iri monthlyReportForm(int activityId) {
        return new Iri(LEGACY_NS + "Activity/" + activityId + "/MonthlyForm");
    }


    public static Iri activityFormSection(int activityId, String categoryName) {
        return new Iri(LEGACY_NS + "/Activity/" + activityId + "/" + categoryName);
    }

    /**
     * @param id the id of the location type
     * @return the IRI of the class for the given location type id
     */
    public static final Iri locationType(int id) {
        return new Iri(LEGACY_NS + "LocationType/" + id);
    }

    /**
     * @param id
     * @return the IRI of the property for the given indicator
     */
    public static Iri indicatorProperty(int id) {
        return new Iri(LEGACY_NS + "indicator/" + id);
    }


    public static Iri attributeGroup(int id) {
        return new Iri(LEGACY_NS + "attributeGroup/" + id);
    }

    public static final Iri PARTNER = new Iri(NS + "Partner");

    public static final String ACTIVITY = NS + "Activity";

    public static final Iri IMPLEMENTED_BY = new Iri(NS + "implementedBy");

    public static final String REPORTS_ON = NS + "reportsOn";

    public static final String REPORTING_PERIOD = NS + "ReportingPeriod";

    public static final String COUNTRY = NS + "country";

    public static final String ADMIN_LEVEL = NS + "AdminLevel";

    public static final String SUB_DIVISION_OF = NS + "subdivisionOf";

    public static final String ADMIN_ENTITY = NS + "AdminEntity";

    public static final Iri LOCATED_AT = new Iri(NS + "locatedAt");

    public static final Iri COMMENTS_PROPERTY = new Iri(LEGACY_NS + "comments");

    public static final String LOCATION = NS + "Location";

    public static final String USER = NS + "User";

    public static final String DATABASE = NS + "Database";

    public static final String OWNS = NS + "owns";


}