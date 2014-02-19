package org.activityinfo.ui.full.client.importer.ui.validation.columns;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.user.cellview.client.Column;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.ui.full.client.importer.draft.DraftInstance;

public class ImportColumn<C> extends Column<DraftInstance, C> {


    public ImportColumn(Cell<C> cell) {
        super(cell);
    }

    @Override
    public C getValue(DraftInstance object) {
        //C value = (C) object.getValue(property.getKey());
        return null;
    }

    @Override
    public String getCellStyleNames(Context context, DraftInstance object) {
        if (object == null) {
            return null;
        }
//        if (object.getValue(property.getKey()) == null) {
//            return "danger";
//        }
        return null;
    }
}
