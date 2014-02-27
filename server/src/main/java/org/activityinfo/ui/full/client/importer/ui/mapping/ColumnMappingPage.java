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
import org.activityinfo.ui.full.client.importer.model.ColumnTarget;
import org.activityinfo.ui.full.client.importer.model.ImportModel;
import org.activityinfo.ui.full.client.importer.ui.mapping.ColumnSelectionChangedEvent.Handler;

/**
 * Page that allows the user to match columns in an imported table to
 * an existing structure.
 */
public class ColumnMappingPage extends ResizeComposite {

    public static int nextUniqueGroupNum = 1;

    private static ColumnMatchingPanelUiBinder uiBinder = GWT
            .create(ColumnMatchingPanelUiBinder.class);

    interface ColumnMatchingPanelUiBinder extends
            UiBinder<Widget, ColumnMappingPage> {
    }

    interface Style extends CssResource {
        String selectedColumn();
    }

    @UiField
    Style style;

    @UiField(provided = true)
    ColumnMappingGrid dataGrid;

    @UiField
    HeadingElement columnChooserHeader;
    @UiField(provided = true)
    ColumnActionChooser actionChooser;

    private final ImportModel importModel;
    private ColumnOptionsFactory optionsFactory;

    private int selectedColumnIndex;

    public ColumnMappingPage(ImportModel importModel, ColumnOptionsFactory optionsFactory) {
        this.importModel = importModel;
        this.optionsFactory = optionsFactory;

        dataGrid = new ColumnMappingGrid(importModel);
        actionChooser = new ColumnActionChooser(optionsFactory.getOptions());

        initWidget(uiBinder.createAndBindUi(this));

        dataGrid.addColumnSelectionChangedHandler(new Handler() {

            @Override
            public void onColumnSelectionChanged(ColumnSelectionChangedEvent e) {
                onColumnChanged(e);
            }
        });

        actionChooser.addValueChangeHandler(new ValueChangeHandler<ColumnTarget>() {

            @Override
            public void onValueChange(ValueChangeEvent<ColumnTarget> event) {
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

    private void updateColumnMapping(ColumnTarget action) {

        importModel.setColumnBinding(action, selectedColumnIndex);
        dataGrid.refreshMappings();
    }
}