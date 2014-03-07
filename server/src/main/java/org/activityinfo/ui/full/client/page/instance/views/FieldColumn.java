package org.activityinfo.ui.full.client.page.instance.views;

import com.google.common.collect.Lists;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import org.activityinfo.api2.shared.Projection;
import org.activityinfo.api2.shared.form.tree.FieldPath;

import java.util.List;

/**
 * Column that displays the value of a given field
 */
public class FieldColumn extends Column<Projection, String> {

    private List<FieldPath> fieldPaths;

    public FieldColumn(FieldPath fieldPath) {
        super(new TextCell());
        this.fieldPaths = Lists.newArrayList(fieldPath);
    }


    @Override
    public String getValue(Projection projection) {
        for(FieldPath path : fieldPaths) {
            Object value = projection.getValue(path);
            if(value != null) {
                return value.toString();
            }
        }
        return "";
    }

    public void addFieldPath(FieldPath path) {
        fieldPaths.add(path);
    }

    public List<FieldPath> getFieldPaths() {
        return fieldPaths;
    }
}
