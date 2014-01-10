package org.activityinfo.client.importer.page;

import org.activityinfo.client.importer.data.ImportRow;

import com.google.gwt.view.client.SelectionModel.AbstractSelectionModel;

public class NullRowSelectionModel extends AbstractSelectionModel<ImportRow> {

	protected NullRowSelectionModel() {
		super(null);
	}

	@Override
	public boolean isSelected(ImportRow object) {
		return false;
	}

	@Override
	public void setSelected(ImportRow object, boolean selected) {
	}

}
