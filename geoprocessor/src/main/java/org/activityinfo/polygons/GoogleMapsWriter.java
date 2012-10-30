package org.activityinfo.polygons;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;

import org.opengis.feature.simple.SimpleFeatureCollection;

import com.google.common.io.Files;
import com.google.gson.stream.JsonWriter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

public class GoogleMapsWriter implements OutputWriter {
	
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	private JsonWriter writer;
	private GooglePolylineEncoder encoder;
	private File outputDir;
	private int adminLevelId;

	public GoogleMapsWriter(File outputDir, int adminLevelId) throws IOException {
		this.outputDir = outputDir;
		this.adminLevelId = adminLevelId;
		
		encoder = new GooglePolylineEncoder();
		writer = new JsonWriter(new OutputStreamWriter(baos));
		//writer.setIndent("  ");
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
			Polygon polygon = (Polygon) geometry.getGeometryN(i);
			writeLinearRing(polygon.getExteriorRing());
			for(int j=0;j!=polygon.getNumInteriorRing();++j) {
				writeLinearRing(polygon.getInteriorRingN(j));
			}
		}
	
		writer.endArray();
		writer.endObject();
	}

	private void writeLinearRing(LineString ring) throws IOException {
		PolylineEncoded encoded = encoder.dpEncode(ring.getCoordinates());
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
		
		// write uncompressed JSON file
		File outFile = new File(outputDir, adminLevelId + ".json");
		Files.write(baos.toByteArray(), outFile);

		// write compressed JSON		
		File compressedOutFile = new File(outputDir, adminLevelId + ".json.gz");
		FileOutputStream fos = new FileOutputStream(compressedOutFile);
		GZIPOutputStream zos = new GZIPOutputStream(fos);
		zos.write(baos.toByteArray());
		zos.finish();
		zos.close();
	}

}
