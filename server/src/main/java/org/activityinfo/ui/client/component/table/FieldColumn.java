package org.activityinfo.ui.client.component.table;

import com.google.common.collect.Lists;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.core.shared.form.tree.FormTree;

import java.util.List;

/**
 * Column that displays the value of a given field
 */
public class FieldColumn extends Column<Projection, String> {

    private FormTree.Node node;
    private List<FieldPath> fieldPaths;
    private String header;

    public FieldColumn(FormTree.Node node) {
        super(new TextCell());
        this.node = node;
        this.header = composeHeader(node);
        this.fieldPaths = Lists.newArrayList(node.getPath());
    }

    public FieldColumn(FieldPath fieldPath, String header) {
        super(new TextCell());
        this.header = header;
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

    public FormTree.Node getNode() {
        return node;
    }

    public List<FieldPath> getFieldPaths() {
        return fieldPaths;
    }

    public String getHeader() {
        return header;
    }

    private String composeHeader(FormTree.Node node) {
        if(node.getPath().isNested()) {
            return node.getDefiningFormClass().getLabel().getValue() + " " + node.getField().getLabel().getValue();
        } else {
            return node.getField().getLabel().getValue();
        }
    }
}
