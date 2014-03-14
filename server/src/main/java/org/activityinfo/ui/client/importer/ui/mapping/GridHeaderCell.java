package org.activityinfo.ui.client.importer.ui.mapping;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.ui.client.importer.data.SourceColumn;
import org.activityinfo.ui.client.importer.model.ColumnTarget;
import org.activityinfo.ui.client.importer.model.ImportModel;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

/**
 * Handles click events on header cells
 */
class GridHeaderCell extends AbstractCell<SourceColumn> {

    private ImportModel model;
    private FieldChoicePresenter options;

    public GridHeaderCell(ImportModel model, FieldChoicePresenter options) {
        super(CLICK);
        this.model = model;
        this.options = options;
    }

    @Override
    public void render(Context context, SourceColumn column, SafeHtmlBuilder sb) {
        if(context.getIndex() == ColumnMappingGrid.SOURCE_COLUMN_HEADER_ROW) {
            sb.appendEscaped(column.getHeader());
        } else {
            ColumnTarget binding = model.getColumnBinding(column.getIndex());
            if(binding == null) {
                sb.appendHtmlConstant(I18N.CONSTANTS.chooseFieldHeading());
            } else if(!binding.isImported()) {
                sb.appendEscaped(I18N.CONSTANTS.ignoreColumnAction());
            } else if(binding.isMapped()) {
                sb.appendEscaped(options.label(binding.getFieldPath()));
            }
        }
    }
}
