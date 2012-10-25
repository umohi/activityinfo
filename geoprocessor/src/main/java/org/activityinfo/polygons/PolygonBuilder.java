package org.activityinfo.polygons;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

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
	
	private static final Logger LOGGER = Logger.getLogger(PolygonBuilder.class.getName());
	
	private int adminLevelId;
	private File propertiesFile;
	private List<AdminEntity> entities;
	private NameMatchingStrategy matchingStrategy;
	private Properties properties;
	private List<OutputWriter> writers = Lists.newArrayList();
	private File geodbRoot;

	public PolygonBuilder(File geodbRoot, File propertiesFile) throws Exception {
		this.geodbRoot = geodbRoot;
		this.propertiesFile = propertiesFile;
		
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
		File outputDir = new File(geodbRoot, "geometry");
		outputDir.mkdirs();
		writers.add(new WkbOutput(outputDir, adminLevelId));
		writers.add(new GoogleMapsWriter(outputDir, adminLevelId));
	}

	private void buildPolygons() throws IOException {
		
		FileDataStore store = FileDataStoreFinder.getDataStore(shapeFilePath());
        SimpleFeatureSource featureSource = store.getFeatureSource();
        SimpleFeatureCollection features = featureSource.getFeatures();
        
        MatchChecker matchChecker = new MatchChecker(entities);
        matchingStrategy.init(featureSource);
     
        for(OutputWriter writer : writers) {
        	writer.start(features);
        }
        
        SimpleFeatureIterator it = features.features();
        while(it.hasNext()) {
        	SimpleFeature feature = it.next();
        	int entityId = matchingStrategy.match(feature);
        	if(entityId != -1) {
	        	
	        	matchChecker.onMatched(entityId);
	        	
	        	Geometry polygon = (Geometry) feature.getDefaultGeometry();
	        	
	        	for(OutputWriter writer : writers) {
	        		writer.write(entityId, polygon);
	        	}
        	}
        }
        
        matchChecker.check();
        
        for(OutputWriter writer : writers) {
        	writer.close();
        }
	}

	private File shapeFilePath() {
		String source = properties.getProperty("source");
		File sourceFile = new File(geodbRoot.getAbsolutePath() + File.separator + "sources" + File.separator + source);
		if(!sourceFile.exists()) {
			throw new RuntimeException("Source file '" + source + "' cannot be found at '" + sourceFile + "'");
		}
		return sourceFile;
	}


	public int getAdminLevelId() {
		return adminLevelId;
	}

}
