package org.activityinfo.ui.full.client.importer.binding;

import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.ui.full.client.importer.data.SourceRow;

/**
* Created by alex on 2/19/14.
*/
public class MatchField {
    private int sourceColumn;
    private FieldPath relativeFieldPath;

    public MatchField(FieldPath relativePath, int sourceColumn) {
        this.relativeFieldPath = relativePath;
        this.sourceColumn = sourceColumn;
    }

    public String getImportedValue(SourceRow row) {
        return row.getColumnValue(sourceColumn);
    }

    public FieldPath getRelativeFieldPath() {
        return relativeFieldPath;
    }
}
