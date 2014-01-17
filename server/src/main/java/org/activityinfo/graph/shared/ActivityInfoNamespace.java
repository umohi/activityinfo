package org.activityinfo.graph.shared;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import org.activityinfo.server.database.hibernate.entity.*;
import org.activityinfo.shared.dto.ActivityDTO;

import java.util.Date;


/**
 * Namespaces for core ActivityInfoNamespace classes and legacy resources
 */
public class ActivityInfoNamespace {

    public static final String NS = "http://www.activityinfo.org/resources/core#";

    public static final String LEGACY_NS = "http://www.activityinfo.org/resources/";
    public static final String RDFS_LABEL = "http://www.w3.org/2000/01/rdf-schema#label";



    public static final String uri(LegacyResource resource) {
        return uri(resource.getClass(), resource.getId());
    }

    public static final <C extends LegacyResource> String uri(Class<C> resourceClass, int id) {
        return LEGACY_NS + lower(resourceClass) + "/" + id;
    }

    private static String lower(Class<? extends LegacyResource> resourceClass) {
        String name = resourceClass.getSimpleName();
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }


    public static final String PARTNER = NS + "Partner";

    public static final String ACTIVITY = NS + "Activity";

    public static final String IMPLEMENTED_BY = NS + "implementedBy";

    public static final String REPORTS_ON = NS + "reportsOn";

    public static final String REPORTING_PERIOD = NS + "ReportingPeriod" ;

    public static final String COUNTRY = NS + "country";

    public static final String ADMIN_LEVEL = NS + "AdminLevel";

    public static final String SUB_DIVISION_OF = NS + "subdivisionOf";

    public static final String ADMIN_ENTITY = NS + "AdminEntity";

    public static final String POPULATED_PLACE = NS + "PopulatedPlace";

    public static final String SCHOOL = NS + "School";

    public static final String LOCATED_AT = NS + "locatedAt";

    public static final String HEALTH_FACILITY = NS + "HealthFacility";

    public static final String WATER_POINT = NS + "WaterPoint";

    public static final String LOCATION = NS + "Location";

    public static final String USER = NS + "User";

    public static final String DATABASE = NS + "Database";

    public static final String OWNS = NS + "owns";

}





