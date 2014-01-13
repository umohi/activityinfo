package org.activityinfo.client.importer.schema;

import java.util.List;

import org.activityinfo.client.importer.ont.DataTypeProperty;
import org.activityinfo.client.importer.ont.ModelBinder;
import org.activityinfo.shared.command.Command;

public class ParentChildBinder<ParentT, ModelT> implements ModelBinder<ParentChild<ParentT, ModelT>> {

	private ModelBinder<ParentT> parentBinder;
	private ModelBinder<ModelT> childBinder;

	public ParentChildBinder(ModelBinder<ParentT> parentBinder, ModelBinder<ModelT> childBinder) {
		this.parentBinder = parentBinder;
		this.childBinder = childBinder;
	}
	
	@Override
	public ParentChild<ParentT, ModelT> newModel() {
		return new ParentChild<ParentT, ModelT>(childBinder.newModel(), parentBinder.newModel());
	}

	@Override
	public Command<?> createCommand(ParentChild<ParentT, ModelT> model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DataTypeProperty<ParentChild<ParentT, ModelT>, ?>> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
