package org.activityinfo.client.importer.schema;

import java.util.List;

import org.activityinfo.client.importer.ont.DataTypeProperty;
import org.activityinfo.client.importer.ont.ModelBinder;
import org.activityinfo.client.importer.ont.ObjectProperty;
import org.activityinfo.client.importer.ont.Property;
import org.activityinfo.shared.command.Command;
import org.hibernate.metamodel.relational.Datatype;

public class SchoolBinder implements ModelBinder<School> {

	private DataTypeProperty<School, String> name;
	private ObjectProperty<School, District> district;
	
	
	@Override
	public School newModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Command<?> createCommand(School model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Property> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
