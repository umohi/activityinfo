package org.activityinfo.client.importer.ont;

import java.util.List;

public abstract class OntClass {

	private String id;
	
	public abstract List<Property> getProperties();
	
}
