package org.activityinfo.ui.desktop.client.widget.editor;

import com.bedatadriven.rebar.bootstrap.client.Bootstrap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class ModalDialog extends PopupPanel {

	private static ModalDialogUiBinder uiBinder = GWT
			.create(ModalDialogUiBinder.class);

	interface ModalDialogUiBinder extends UiBinder<Widget, ModalDialog> {
	}


	@UiField Bootstrap b = Bootstrap.INSTANCE;
	
	public ModalDialog() {
		setWidget(uiBinder.createAndBindUi(this));
		setStyleName(b.modal());
	}
}
