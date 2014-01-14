package org.activityinfo.client.importer.page;

import org.activityinfo.client.importer.schema.SchemaImporter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class ImportSchemaView extends Composite {

	private static ImportSchemaUiBinder uiBinder = GWT
			.create(ImportSchemaUiBinder.class);

	interface ImportSchemaUiBinder extends UiBinder<Widget, ImportSchemaView> {
	}

	private SchemaImporter importer;

	public ImportSchemaView(SchemaImporter importer) {
		this.importer = importer;
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField TextArea textArea;
	@UiField HTML warnings;

	@UiHandler("textArea")
	public void onTextAreaChanged(ChangeEvent changeEvent) {
		importer.clearWarnings();
	}

}
