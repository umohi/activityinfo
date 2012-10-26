package org.activityinfo.polygons;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Logger;

import org.activityinfo.GeoTask;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.FilterVisitor;

import com.google.common.collect.Lists;

public class GaulPolygonTask implements GeoTask {

	private File root;
	private Properties properties;
	
	private Logger LOGGER = Logger.getLogger(GaulPolygonTask.class.getName());

	public void run(String geodbRoot) throws Exception {
		this.root = new File(geodbRoot);
		this.properties = new Properties();
		File propertiesFile = new File(root.getAbsolutePath() + "/admin/GAUL/level1.properties");
		this.properties.load(new FileInputStream(propertiesFile));
		
		for (Level level : readLevels()) {
			LOGGER.info("Processing " + level.gaulCountryName);
			PolygonBuilder builder = new PolygonBuilder(root, propertiesFile);
			builder.setAdminLevelId(level.adminLevelId);
			builder.setFilter(filterByCountry(level.gaulCountryName));
			builder.run();	
		}
	}
	
	private List<Level> readLevels() throws IOException {
		Properties props = new Properties();
		List<Level> countries = Lists.newArrayList();
		props.load(new FileInputStream(root + File.separator + "admin" + File.separator + "GAUL" + File.separator + "level1.properties"));
		for(Entry<Object, Object> entry : props.entrySet()) {
			String key = (String)entry.getKey();
			if(key.matches("\\d+")) {
				String name = (String)entry.getValue();
				countries.add(new Level(Integer.parseInt(key), name.trim()));
			}
		}
		return countries;
	}
	

	private Filter filterByCountry(String countryName) {
		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2( GeoTools.getDefaultHints() );
		String propertyName = properties.getProperty("country.column");
		return ff.equal( ff.property( propertyName), ff.literal(countryName), false);
	}

	
	private class Level {
		private int adminLevelId;
		private String gaulCountryName;
		
		public Level(int adminLevelId, String gaulCountryName) {
			super();
			this.adminLevelId = adminLevelId;
			this.gaulCountryName = gaulCountryName;
		}	
	}
	
}
