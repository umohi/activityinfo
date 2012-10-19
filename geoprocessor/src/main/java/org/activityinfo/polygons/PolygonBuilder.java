package org.activityinfo.polygons;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryType;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygonal;
import com.vividsolutions.jts.io.OutStream;
import com.vividsolutions.jts.io.OutputStreamOutStream;
import com.vividsolutions.jts.io.WKBWriter;

public class PolygonBuilder {
	
	private int adminLevelId;
	private File propertiesFile;
	private List<AdminEntity> entities;
	private NameMatchingStrategy matchingStrategy;
	private Properties properties;

	public PolygonBuilder(String propertiesPath) throws Exception {
		propertiesFile = new File(propertiesPath);
		
		properties = new Properties();
		properties.load(new FileInputStream(propertiesFile));
		
		this.adminLevelId = Integer.parseInt(properties.getProperty("adminLevelId"));
	
		fetchEntities();
		initMatchingStrategy();
		buildPolygons();
	}
	

	private void fetchEntities() throws Exception {
		URL url = new URL("http://localhost:8888/api/AdminEntities?levelId=" + adminLevelId);
		Gson gson = new Gson();
		this.entities = gson.fromJson(Resources.toString(url, Charsets.UTF_8), AdminEntityResult.class).getData();
		System.err.println("Fetched " + entities.size() + " entities for level " + adminLevelId);
	}
	

	private void initMatchingStrategy() {
		String strategyName = properties.getProperty("match", "name");
		if(strategyName.equals("name")) {
			matchingStrategy = new NameMatchingStrategy(
					entities,
					properties);
		} else {
			throw new UnsupportedOperationException("invalid matching strategy: " + matchingStrategy);
		}
	}

	private void buildPolygons() throws IOException {
		
		FileDataStore store = FileDataStoreFinder.getDataStore(shapeFilePath());
        SimpleFeatureSource featureSource = store.getFeatureSource();
        SimpleFeatureCollection features = featureSource.getFeatures();
        
        matchingStrategy.init(featureSource);
       
        List<String> attributes = new ArrayList<String>();
        for(AttributeDescriptor attribute : featureSource.getSchema().getAttributeDescriptors()) {
        	if(!(attribute.getType() instanceof GeometryType)) {
        		attributes.add(attribute.getName().getLocalPart());
	        	System.out.print(attribute.getName());
	        	System.out.print(",");
        	}
        }
        System.out.println("X1,Y1,X2,Y2");
        
        DataOutputStream wkbOut = new DataOutputStream(new FileOutputStream(wkbFile()));
        wkbOut.writeInt(features.size());
        
        SimpleFeatureIterator it = features.features();
        while(it.hasNext()) {
        	SimpleFeature feature = it.next();
        	int entityId = matchingStrategy.match(feature);
        	Polygonal polygon = (Polygonal) feature.getDefaultGeometry();
        	
        	wkbOut.writeInt(entityId);
        
        	WKBWriter writer = new WKBWriter();
        	Geometry geometry = (Geometry) feature.getDefaultGeometry();
			writer.write(geometry, new OutputStreamOutStream(wkbOut));
        	
        	
        	System.out.println(entityId + " => " + polygon);
//        	
//        	StringBuilder line = new StringBuilder();
//        	for(String attribute : attributes){
//        		line.append(feature.getAttribute(attribute)).append(",");
//        	}
//        	line.append(
//        			feature.getBounds().getMinX() + "," +
//        			feature.getBounds().getMinY() + "," +
//        			feature.getBounds().getMaxX() + "," +
//        			feature.getBounds().getMaxY());
//        	System.out.println(line.toString());
        }
        
        wkbOut.close();
	}

	private File wkbFile() {
		return new File(propertiesFile.getParentFile(), adminLevelId + ".wkb");
	}


	private File shapeFilePath() {
		String name = propertiesFile.getName().replace(".aiload", ".shp");
		return new File(propertiesFile.getParentFile(), name);
	}

}
