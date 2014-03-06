package org.activityinfo.api2.shared;


/**
 * Namespaces for core Namespace classes and legacy resources
 */
public class Namespace {

    public static final String NS = "http://www.activityinfo.org/resources/core#";

    public static final String LEGACY_NS = "http://www.activityinfo.org/resources/";

    public static final Iri RDFS_LABEL = new Iri("http://www.w3.org/2000/01/rdf-schema#label");

    private Namespace() {
    }


    public static final Iri REPORTED_BY = new Iri(NS + "reportedBy");


    public static final String REPORTING_PERIOD = NS + "ReportingPeriod";



}