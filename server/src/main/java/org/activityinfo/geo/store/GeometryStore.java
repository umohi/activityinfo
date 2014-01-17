package org.activityinfo.geo.store;


import com.vividsolutions.jts.geom.Geometry;

/**
 * Provides storage of geometry of RDF resources
 */
public interface GeometryStore {

    Geometry getGeometry(String uri);

    void putGeometry(String uri, Geometry geometry);

}
