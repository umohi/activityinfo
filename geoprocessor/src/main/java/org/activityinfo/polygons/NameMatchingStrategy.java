package org.activityinfo.polygons;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class NameMatchingStrategy {
	
	private static final Logger LOGGER = Logger.getLogger(NameMatchingStrategy.class.getName());
	
	private Properties properties;
	private String nameColumn;
	private Map<String, Integer> nameMap = Maps.newHashMap();

	public NameMatchingStrategy(List<AdminEntity> entities, Properties properties) {
		this.properties = properties;
		
		this.nameColumn = properties.getProperty("name.column");
		
		for(AdminEntity entity : entities) {
			String key = key(entity.getName());
			if(nameMap.containsKey(key)) {
				throw new UnsupportedOperationException("Cannot match by name, there are duplicates: '" + key + "'");
			}
 			nameMap.put(key, entity.getId());
		}
	}

	

	private String key(String name) {
		return name.toLowerCase().trim();
	}


	public void init(SimpleFeatureSource featureSource) {
		
	}



	public int match(SimpleFeature feature) {
		String name = (String) feature.getAttribute(nameColumn);
		
		if(isIgnored(name)) {
			LOGGER.warning("Ignoring '" + name + "'");
			return -1;
		}
		
		String alias = null;
		Integer id = nameMap.get(key(name));
		if(id == null) {
			alias = getAlias(name);
			if(alias != null) {
				id = nameMap.get(key(alias));
			}
		}
		if(id == null) {
			onUnmatched(name, alias);
		}
		return id;
	}



	private String getAlias(String name) {
		return Strings.emptyToNull(properties.getProperty("alias." + name));
	}

	private boolean isIgnored(String name) {
		return !Strings.isNullOrEmpty(properties.getProperty("ignore." + name));
	}


	private void onUnmatched(String name, String alias) {
		if(alias != null) {
			System.err.println("Unmatched alias '" + alias + "' , (original '" + name + "'), possible matches:");
		} else {
			System.err.println("Unmatched name '" + name + "', possible matches:");
		}
		List<String> names = Lists.newArrayList(nameMap.keySet());
		Collections.sort(names);
		for(String keyName : names) {
			System.err.println("\t" + keyName);
		}
		
		throw new MatchException("unmatched name: '" + name + "'");

	}
	

}
