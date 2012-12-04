package org.activityinfo.polygons;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.opengis.feature.simple.SimpleFeatureCollection;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class SqlBoundsOutput implements OutputWriter {

	private PrintWriter writer;
	
	public SqlBoundsOutput(File outputDir, int adminLevelId) throws FileNotFoundException {
		writer = new PrintWriter(new File(outputDir, adminLevelId + ".bounds.sql"));
	}
	
	public void start(SimpleFeatureCollection features) throws IOException {
		
	}

	public void write(int adminEntityId, Geometry geometry) throws IOException {
		Envelope box = geometry.getEnvelopeInternal();
		writer.println(String.format("UPDATE adminentity SET x1=%f, y1=%f, x2=%f, y2=%f WHERE AdminEntityId=%d;",
				box.getMinX(),
				box.getMinY(),
				box.getMaxX(),
				box.getMaxY(),
				adminEntityId));
	}

	public void close() throws IOException {
		writer.close();
	}

}
