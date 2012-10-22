package org.activityinfo.polygons;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.geotools.data.simple.SimpleFeatureCollection;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.OutputStreamOutStream;
import com.vividsolutions.jts.io.WKBWriter;

public class WkbOutput implements OutputWriter {

	private DataOutputStream dataOut;
	private WKBWriter writer = new WKBWriter();


	public WkbOutput(File outputDir, int adminLevelId) throws FileNotFoundException {
        dataOut = new DataOutputStream(new FileOutputStream(new File(outputDir, adminLevelId + ".wkb")));
	}
	
	public void start(SimpleFeatureCollection features) throws IOException {
        dataOut.writeInt(features.size());
		
	}
	
	public void write(int adminEntityId, Geometry geometry) throws IOException {
		dataOut.writeInt(adminEntityId);		
		writer.write(geometry, new OutputStreamOutStream(dataOut));
	}

	public void close() throws IOException {
		dataOut.close();
	}
 
}
