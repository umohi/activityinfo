package org.activityinfo.ui.full.client.importer.ui.mapping;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.ui.full.client.importer.model.ColumnAction;
import org.activityinfo.ui.full.client.importer.model.ImportModel;
import org.activityinfo.ui.full.client.importer.ui.mapping.ColumnSelectionChangedEvent.Handler;

/**
 * Page that allows the user to match columns in an imported table to
 * an existing structure.
 */
public class ColumnMappingPage<T> extends ResizeComposite {

    public static int nextUniqueGroupNum = 1;

    private static ColumnMatchingPanelUiBinder uiBinder = GWT
            .create(ColumnMatchingPanelUiBinder.class);

    interface ColumnMatchingPanelUiBinder extends
            UiBinder<Widget, ColumnMappingPage<?>> {
    }

    interface Style extends CssResource {
        String selectedColumn();
    }

    @UiField
    Style style;

    @UiField(provided = true)
    ColumnMappingGrid<T> dataGrid;

    @UiField
    HeadingElement columnChooserHeader;
    @UiField(provided = true)
    ColumnActionChooser actionChooser;

    private final ImportModel<T> importModel;

    private int selectedColumnIndex;

    public ColumnMappingPage(ImportModel<T> importModel) {
        this.importModel = importModel;

        dataGrid = new ColumnMappingGrid<T>(importModel);
        actionChooser = new ColumnActionChooser(importModel.getColumnActions());

        initWidget(uiBinder.createAndBindUi(this));

        dataGrid.addColumnSelectionChangedHandler(new Handler() {

            @Override
            public void onColumnSelectionChanged(ColumnSelectionChangedEvent e) {
                onColumnChanged(e);
            }
        });

        actionChooser.addValueChangeHandler(new ValueChangeHandler<ColumnAction>() {

            @Override
            public void onValueChange(ValueChangeEvent<ColumnAction> event) {
                updateColumnMapping(event.getValue());
            }
        });
    }

    public void refresh() {
        dataGrid.refresh();
    }

    private void onColumnChanged(ColumnSelectionChangedEvent e) {
        selectedColumnIndex = e.getSelectedColumnIndex();
        actionChooser.setValue(importModel.getColumnBindings().get(selectedColumnIndex), false);
        columnChooserHeader.setInnerText(importModel.getSource().getColumnHeader(selectedColumnIndex));
    }

    private void updateColumnMapping(ColumnAction action) {


        importModel.setColumnBinding(action, selectedColumnIndex);

        dataGrid.refreshMappings();
    }
}