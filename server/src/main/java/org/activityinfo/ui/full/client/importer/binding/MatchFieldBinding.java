package org.activityinfo.ui.full.client.importer.binding;

import org.activityinfo.api2.shared.Projection;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.ui.full.client.importer.converter.Converter;
import org.activityinfo.ui.full.client.importer.data.SourceRow;
import org.activityinfo.ui.full.client.importer.match.FieldValueScorer;

/**
* Binds an {@code ImportColumn} to a data field of a referenced {@code FormClass} for
 * use in matching an {@code ImportRow} to a referenced {@code FormInstance}
*/
public class MatchFieldBinding {
    private int index;
    private int sourceColumn;
    private FormTree.Node node;
    private FieldPath relativeFieldPath;
    private Converter converter;
    private FieldValueScorer scorer;

    public MatchFieldBinding(int index,
                             FormTree.Node node,
                             FieldPath relativePath,
                             int sourceColumn,
                             Converter converter,
                             FieldValueScorer scorer) {
        this.index = index;
        this.node = node;
        this.relativeFieldPath = relativePath;
        this.sourceColumn = sourceColumn;
        this.converter = converter;
        this.scorer = scorer;
    }

    public int getIndex() {
        return index;
    }

    public Object getImportedValue(SourceRow row) {
        return converter.convertString(row.getColumnValue(sourceColumn));
    }

    public int getSourceColumn() {
        return sourceColumn;
    }

    public void setSourceColumn(int sourceColumn) {
        this.sourceColumn = sourceColumn;
    }

    public FieldPath getRelativeFieldPath() {
        return relativeFieldPath;
    }

    public Object getReferencedValue(Projection projection) {
        return projection.getValue(relativeFieldPath);
    }

    public FieldValueScorer getScorer() {
        return scorer;
    }

    public FormTree.Node getNode() {
        return node;
    }
}
