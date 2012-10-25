package org.activityinfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.activityinfo.polygons.PolygonTask;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryType;

public class Geotools {

	public static void main(String[] args) throws Exception {
		if(args.length > 1 && args[0].equals("bbox")) {
			File shapeFile = new File(args[1]);
			extractBBox(shapeFile);	
		} else if(args.length > 1 && args[0].equals("polygons")) {
			new PolygonTask().run(args[1]);
		} else {
			System.err.println("Usage: geotools <command> [options...]");
			System.err.println();
			System.err.println("Available commands:");
			System.err.println("\tpolygons <geodb root>");
			System.err.println("\tbbox shapefile");
			System.exit(-1);
		}
	}

	private static void extractBBox(File path) throws IOException {
		FileDataStore store = FileDataStoreFinder.getDataStore(path);
        SimpleFeatureSource featureSource = store.getFeatureSource();
        SimpleFeatureCollection features = featureSource.getFeatures();
        
        List<String> attributes = new ArrayList<String>();
        for(AttributeDescriptor attribute : featureSource.getSchema().getAttributeDescriptors()) {
        	if(!(attribute.getType() instanceof GeometryType)) {
        		attributes.add(attribute.getName().getLocalPart());
	        	System.out.print(attribute.getName());
	        	System.out.print(",");
        	}
        }
        System.out.println("X1,Y1,X2,Y2");
        
        SimpleFeatureIterator it = features.features();
        while(it.hasNext()) {
        	SimpleFeature feature = it.next();
        	StringBuilder line = new StringBuilder();
        	for(String attribute : attributes){
        		line.append(feature.getAttribute(attribute)).append(",");
        	}
        	line.append(
        			feature.getBounds().getMinX() + "," +
        			feature.getBounds().getMinY() + "," +
        			feature.getBounds().getMaxX() + "," +
        			feature.getBounds().getMaxY());
        	System.out.println(line.toString());
        }

	}
	
}
