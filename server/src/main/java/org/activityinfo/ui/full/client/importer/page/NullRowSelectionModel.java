package org.activityinfo.ui.full.client.importer.page;

import com.google.gwt.view.client.SelectionModel.AbstractSelectionModel;
import org.activityinfo.ui.full.client.importer.data.SourceRow;

public class NullRowSelectionModel extends AbstractSelectionModel<SourceRow> {

    protected NullRowSelectionModel() {
        super(null);
    }

    @Override
    public boolean isSelected(SourceRow object) {
        return false;
    }

    @Override
    public void setSelected(SourceRow object, boolean selected) {
    }

}
