package org.activityinfo.ui.full.client.importer.ui.validation.columns;

import com.google.gwt.cell.client.TextCell;
import org.activityinfo.api2.shared.LocalizedString;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.ui.full.client.importer.binding.MappedReferenceFieldBinding;
import org.activityinfo.ui.full.client.importer.draft.DraftFieldValue;
import org.activityinfo.ui.full.client.importer.draft.DraftInstance;
import org.activityinfo.ui.full.client.importer.match.ValueStatus;
import org.activityinfo.ui.full.client.importer.ui.validation.columns.ImportColumn;

/**
 * A column that is mapped to a nested data field.
 */
public class NestedFieldColumn extends ImportColumn {

    private MappedReferenceFieldBinding referenceBinding;
    private FormTree.Node field;

    public NestedFieldColumn(MappedReferenceFieldBinding referenceBinding, FormTree.Node field) {
        super(new TextCell());
        this.referenceBinding = referenceBinding;
        this.field = field;
    }




}
