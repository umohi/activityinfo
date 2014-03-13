package org.activityinfo.ui.full.client.importer.ui.validation.columns;

import com.google.common.base.Function;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.ui.full.client.importer.binding.MappedReferenceFieldBinding;
import org.activityinfo.ui.full.client.importer.binding.MatchFieldBinding;
import org.activityinfo.ui.full.client.importer.data.SourceRow;
import org.activityinfo.ui.full.client.importer.match.ScoredReference;
import org.activityinfo.ui.full.client.importer.ui.validation.cells.ValidationCellTemplates;

/**
 * A column that is mapped to a nested data field.
 */
public class MatchedFieldColumn extends ImportColumn<SafeHtml> {

    private ValidationCellTemplates templates;
    private MappedReferenceFieldBinding referenceBinding;
    private MatchFieldBinding fieldBinding;
    private Function<Object, String> renderer;

    public MatchedFieldColumn(ValidationCellTemplates templates,
                              MappedReferenceFieldBinding referenceBinding,
                              MatchFieldBinding fieldBinding,
                              Function<Object, String> renderer) {
        super(new SafeHtmlCell());
        this.templates = templates;
        this.referenceBinding = referenceBinding;
        this.fieldBinding = fieldBinding;
        this.renderer = renderer;
    }


    @Override
    public SafeHtml getValue(SourceRow row) {
        ScoredReference match = referenceBinding.getMatchTable().getMatch(row.getRowIndex());
        Object importedValue = fieldBinding.getImportedValue(row);

        if(match == null) {
            return templates.invalid(renderer.apply(importedValue));
        } else {
            Object referencedValue = fieldBinding.getReferencedValue(match.getProjection());
            double score = match.getScore(fieldBinding.getIndex());
            if(score == 1.0) {
                return SafeHtmlUtils.fromString(renderer.apply(importedValue));
            } else {
                SafeHtmlBuilder sb = new SafeHtmlBuilder();
                sb.append(templates.deleted(renderer.apply(importedValue)));
                sb.append(templates.inserted(renderer.apply(referencedValue)));
                return sb.toSafeHtml();
            }
        }
    }

    @Override
    public String getHeader() {
        return fieldBinding.getNode().debugPath();
    }

    @Override
    public int getSourceColumn() {
        return fieldBinding.getSourceColumn();
    }

    @Override
    public FieldPath getFieldPath() {
        return fieldBinding.getNode().getPath();
    }
}
