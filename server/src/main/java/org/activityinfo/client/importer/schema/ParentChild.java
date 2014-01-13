package org.activityinfo.client.importer.schema;

public class ParentChild<ParentT, ModelT> {

	private ModelT model;
	private ParentT parent;
	
	public ParentChild(ModelT model, ParentT parent) {
		super();
		this.model = model;
		this.parent = parent;
	}
	
}
