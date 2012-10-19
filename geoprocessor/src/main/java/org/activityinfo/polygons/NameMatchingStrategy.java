package org.activityinfo.polygons;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;

import com.google.common.collect.Maps;

public class NameMatchingStrategy {
	
	private Properties properties;
	private List<AdminEntity> entities;
	private String nameColumn;
	private Map<String, Integer> nameMap = Maps.newHashMap();

	public NameMatchingStrategy(List<AdminEntity> entities, Properties properties) {
		this.entities = entities;
		this.properties = properties;
		
		this.nameColumn = properties.getProperty("name.column");
		
		for(AdminEntity entity : entities) {
			nameMap.put(entity.getName(), entity.getId());
		}
	}

	

	public void init(SimpleFeatureSource featureSource) {
		
	}



	public int match(SimpleFeature feature) {
		String name = (String) feature.getAttribute(nameColumn);
		Integer id = nameMap.get(name);
		if(id == null) {
			throw new MatchException(feature.toString());
		}
		return id;
	}
	

}
