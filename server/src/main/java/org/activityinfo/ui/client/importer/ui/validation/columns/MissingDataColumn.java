package org.activityinfo.ui.client.importer.ui.validation.columns;

import com.google.common.base.Function;
import com.google.gwt.cell.client.EditTextCell;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.ui.client.importer.binding.MissingFieldBinding;
import org.activityinfo.ui.client.importer.data.SourceRow;

/**
 * Placeholder column for a field that is missing a mapping to the
 * {@code SourceTable}
 */
public class MissingDataColumn extends ImportColumn<String> {
    private MissingFieldBinding binding;
    private Function<Object, String> renderer;

    public MissingDataColumn(MissingFieldBinding binding, Function<Object, String> renderer) {
        super(new EditTextCell());

        this.binding = binding;
        this.renderer = renderer;

    }

    @Override
    public String getValue(SourceRow object) {
        return renderer.apply(binding.getProvidedValue());
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
        return new FieldPath(binding.getFieldId());
    }
}
