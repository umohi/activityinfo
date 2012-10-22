package org.activityinfo.polygons;

import java.io.IOException;

import org.geotools.data.simple.SimpleFeatureCollection;

import com.vividsolutions.jts.geom.Geometry;

public interface OutputWriter {

	void start(SimpleFeatureCollection features) throws IOException;
	void write(int adminEntityId, Geometry geometry) throws IOException;
	void close() throws IOException;
}
