package org.activityinfo.ui.client.component.importDialog.validation.columns;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.core.shared.importing.binding.MissingFieldBinding;
import org.activityinfo.ui.client.component.importDialog.data.SourceRow;

/**
 * Column for a missing reference field
 */
public class MissingReferenceColumn extends ImportColumn<String> {

    private final MissingFieldBinding binding;

    public MissingReferenceColumn(MissingFieldBinding binding) {
        super(new ClickableTextCell());
        this.binding = binding;
    }

    public MissingFieldBinding getBinding() {
        return binding;
    }

    @Override
    public String getValue(SourceRow object) {
        return "";
    }

    @Override
    public String getHeader() {
        return binding.getFieldNode().debugPath();
    }

    @Override
    public int getSourceColumn() {
        return -1;
    }

    @Override
    public FieldPath getFieldPath() {
        return binding.getFieldNode().getPath();
    }

    @Override
    public FieldUpdater<SourceRow, String> getFieldUpdater() {
        return new FieldUpdater<SourceRow, String>() {
            @Override
            public void update(int index, SourceRow object, String value) {

            }
        };
    }
}
