package org.activityinfo.polygons;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.vividsolutions.jts.geom.Geometry;

public class PolygonBuilder {
	
	private int adminLevelId;
	private File propertiesFile;
	private List<AdminEntity> entities;
	private NameMatchingStrategy matchingStrategy;
	private Properties properties;
	private List<OutputWriter> writers = Lists.newArrayList();

	public PolygonBuilder(String propertiesPath) throws Exception {
		propertiesFile = new File(propertiesPath);
		
		properties = new Properties();
		properties.load(new FileInputStream(propertiesFile));
		
		this.adminLevelId = Integer.parseInt(properties.getProperty("adminLevelId"));
	
		fetchEntities();
		initMatchingStrategy();
		initWriters();
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
	

	private void initWriters() throws IOException {
		File outputDir = propertiesFile.getParentFile();
		writers.add(new WkbOutput(outputDir, adminLevelId));
		writers.add(new GoogleMapsWriter(outputDir, adminLevelId));
	}



	private void buildPolygons() throws IOException {
		
		FileDataStore store = FileDataStoreFinder.getDataStore(shapeFilePath());
        SimpleFeatureSource featureSource = store.getFeatureSource();
        SimpleFeatureCollection features = featureSource.getFeatures();
        
        matchingStrategy.init(featureSource);
     
        for(OutputWriter writer : writers) {
        	writer.start(features);
        }
        
        SimpleFeatureIterator it = features.features();
        while(it.hasNext()) {
        	SimpleFeature feature = it.next();
        	int entityId = matchingStrategy.match(feature);
        	Geometry polygon = (Geometry) feature.getDefaultGeometry();
        	
        	for(OutputWriter writer : writers) {
        		writer.write(entityId, polygon);
        	}
        	
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
        
        for(OutputWriter writer : writers) {
        	writer.close();
        }
	}

	private File shapeFilePath() {
		String name = propertiesFile.getName().replace(".aiload", ".shp");
		return new File(propertiesFile.getParentFile(), name);
	}

}
