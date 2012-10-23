package org.activityinfo.polygons;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.geotools.data.simple.SimpleFeatureCollection;

import com.google.gson.stream.JsonWriter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class GoogleMapsWriter implements OutputWriter {
	
	private JsonWriter writer;
	private GooglePolylineEncoder encoder = new GooglePolylineEncoder();

	public GoogleMapsWriter(File outputDir, int adminLevelId) throws IOException {
		writer = new JsonWriter(new FileWriter(new File(outputDir, adminLevelId + ".json")));
		writer.setIndent("  ");
	
	}
	
	public void start(SimpleFeatureCollection features) throws IOException {
		writer.beginObject();
		writer.name("zoomFactor");
		writer.value(encoder.getZoomFactor());
		writer.name("numLevels");
		writer.value(encoder.getNumLevels());
		writer.name("entities");
		writer.beginArray();
	}

	public void write(int adminEntityId, Geometry geometry) throws IOException {
		writer.beginObject();
		writer.name("id");
		writer.value(adminEntityId);
		writer.name("polygons");
		writer.beginArray();
		
		for(int i=0;i!=geometry.getNumGeometries();++i) {
			writePolygon(geometry.getGeometryN(i));
		}
		
		writer.endArray();
		writer.endObject();
	}

	private void writePolygon(Geometry geometryN) throws IOException {
		PolylineEncoded encoded = encoder.dpEncode(closeLinearRing(geometryN.getCoordinates()));
		writer.beginObject();
		writer.name("points");	
		writer.value(encoded.getPoints());
		writer.name("levels");
		writer.value(encoded.getLevels());
		writer.endObject();
	}

	public void close() throws IOException {
		writer.endArray();
		writer.endObject();
		writer.close();
	}
	
	private Coordinate[] closeLinearRing(Coordinate[] coordinates) {
		if(coordinates[0].equals(coordinates[coordinates.length-1])) {
			return coordinates;
		} else {
			Coordinate[] closed = Arrays.copyOf(coordinates, coordinates.length+1);
			closed[coordinates.length] = new Coordinate(coordinates[0]);
			return closed;
		}
	}

}
