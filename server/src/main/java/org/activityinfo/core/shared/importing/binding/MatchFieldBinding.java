package org.activityinfo.core.shared.importing.binding;

import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.core.shared.form.tree.FormTree;
import org.activityinfo.core.shared.importing.SourceRow;
import org.activityinfo.core.shared.importing.match.FieldValueScorer;
import org.activityinfo.core.shared.type.converter.Converter;

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
        return converter.convert(row.getColumnValue(sourceColumn));
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
