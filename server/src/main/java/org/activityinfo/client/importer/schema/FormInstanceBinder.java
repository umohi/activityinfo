package org.activityinfo.client.importer.schema;

import java.util.List;

import org.activityinfo.client.importer.ont.DataTypeProperty;
import org.activityinfo.client.importer.ont.ModelBinder;
import org.activityinfo.client.importer.ont.ObjectProperty;
import org.activityinfo.client.importer.ont.Property;
import org.activityinfo.shared.command.Command;
import org.activityinfo.shared.dto.SiteDTO;

public class FormInstanceBinder implements ModelBinder<SiteDTO> {

	private ObjectProperty<SiteDTO, School> objectType;

	private DataTypeProperty<SiteDTO, Double> indicator1;
	private DataTypeProperty<SiteDTO, Double> indicator2;
	private DataTypeProperty<SiteDTO, Double> indicator3;
	
	
	
	@Override
	public SiteDTO newModel() {
		return new SiteDTO();
	}

	@Override
	public Command<?> createCommand(SiteDTO model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Property> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

}
