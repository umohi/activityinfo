package org.activityinfo.ui.full.client.importer.page;

import com.google.gwt.view.client.SelectionModel.AbstractSelectionModel;
import org.activityinfo.ui.full.client.importer.data.ImportRow;

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
