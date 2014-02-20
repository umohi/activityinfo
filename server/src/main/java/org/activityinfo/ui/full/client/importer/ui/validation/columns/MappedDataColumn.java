package org.activityinfo.ui.full.client.importer.ui.validation.columns;

import com.google.common.base.Function;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.ui.full.client.importer.binding.MappedDataFieldBinding;
import org.activityinfo.ui.full.client.importer.data.SourceRow;
import org.activityinfo.ui.full.client.importer.ui.validation.cells.MappedDataCell;
import org.activityinfo.ui.full.client.importer.ui.validation.cells.ValidationCellTemplates;

/**
 * Column that has been mapped to a simple data field
 */
public class MappedDataColumn extends ImportColumn<SafeHtml> {

    private ValidationCellTemplates templates;
    private final MappedDataFieldBinding binding;
    private final Function<Object, String> renderer;

    public MappedDataColumn(ValidationCellTemplates templates,
                            MappedDataFieldBinding binding, Function<Object, String> renderer) {
        super(new SafeHtmlCell());
        this.templates = templates;
        this.binding = binding;
        this.renderer = renderer;
    }

    @Override
    public SafeHtml getValue(SourceRow row) {
        String importedValue = binding.getImportedValue(row);
        if(importedValue == null) {
            return SafeHtmlUtils.EMPTY_SAFE_HTML;
        }
        Object convertedValue;
        try {
            convertedValue = binding.convert(importedValue);
        } catch(Exception e) {
            return templates.invalid(importedValue);
        }
        return SafeHtmlUtils.fromString(renderer.apply(convertedValue));
    }

    @Override
    public String getHeader() {
        return binding.getField().getLabel().getValue();
    }

    @Override
    public int getSourceColumn() {
        return binding.getSourceColumn();
    }

    @Override
    public FieldPath getFieldPath() {
        return new FieldPath(binding.getFieldId());
    }
}
