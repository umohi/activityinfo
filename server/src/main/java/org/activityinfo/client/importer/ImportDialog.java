package org.activityinfo.client.importer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog box that contains the steps of the import process
 *
 */
public class ImportDialog extends PopupPanel {

	private static ImportDialogUiBinder uiBinder = GWT
			.create(ImportDialogUiBinder.class);

	interface ImportDialogUiBinder extends UiBinder<Widget, ImportDialog> {
	}

	@UiField DockLayoutPanel dockLayoutPanel;
	
	@UiField Button cancelButton;
	
	@UiField Button nextButton;
	
	@UiField SimpleLayoutPanel pagePanel;
	
	public ImportDialog() {
		setWidget(uiBinder.createAndBindUi(this));
	}
	
	public Button getNextButton() {
		return nextButton;
	}
	
	public HasClickHandlers getCancelButton() {
		return cancelButton;
	}

	public void setPage(IsWidget page) {
		pagePanel.setWidget(page);
	}
}
