package org.activityinfo.client.importer.ont;

import java.util.List;

import com.google.common.collect.Lists;

public class PropertyPath {

	public List<Property> path;
	
	public PropertyPath(List<Property> prefix, Property property) {
		path = Lists.newArrayList();
		path.addAll(prefix);
		path.add(property);
	}

	public String getKey() {
		StringBuilder sb = new StringBuilder();
		for(Property property : path) {
			sb.append(".");
			sb.append(property.getId());
		}
		return sb.toString();
	}
	
	public String getLabel() {
		StringBuilder sb = new StringBuilder();
		for(Property property : path) {
			sb.append(" / ");
			sb.append(property.getLabel());
		}
		return sb.toString();
	}
	
	public DataTypeProperty<?, ?> getDatatypeProperty() {
		return (DataTypeProperty<?, ?>)path.get(path.size() - 1);
	}	
}
