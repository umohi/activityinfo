package org.activityinfo.client.importer.ont;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;


public class OntUtils {


	public List<PropertyPath> getAll(ModelBinder<?> binder) {
		List<PropertyPath> properties = Lists.newArrayList();
		findAll(properties, Collections.<Property>emptyList(), binder);
		return properties;
	}

	private void findAll(List<PropertyPath> properties, List<Property> prefix, ModelBinder<?> binder) {
		for(Property property : binder.getProperties()) {
			if(property instanceof DataTypeProperty) {
				properties.add(new PropertyPath(prefix, property));
			} else {
				ObjectProperty objectProperty = (ObjectProperty) property;
				List<Property> newPrefix = Lists.newArrayList(prefix);
				newPrefix.add(objectProperty);
				
				findAll(properties, newPrefix, binder);
			}
		}	
	}
}
