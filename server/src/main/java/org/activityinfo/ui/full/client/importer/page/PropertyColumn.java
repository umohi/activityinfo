package org.activityinfo.ui.full.client.importer.page;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.user.cellview.client.Column;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.ui.full.client.importer.draft.DraftInstance;

public class PropertyColumn<C> extends Column<DraftInstance, C> {

    private final FieldPath property;

    public PropertyColumn(FieldPath property, Cell<C> cell) {
        super(cell);
        this.property = property;
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
