package org.activityinfo.ui.full.client.importer.ui.mapping;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.activityinfo.ui.full.client.importer.data.SourceColumn;
import org.activityinfo.ui.full.client.importer.model.ColumnTarget;
import org.activityinfo.ui.full.client.importer.model.ImportModel;
import org.activityinfo.ui.full.client.importer.ui.ImportPage;
import org.activityinfo.ui.full.client.widget.Panel;

/**
 * Page that allows the user to match columns in an imported table to
 * {@code FormField}s in an existing {@code FormClass}
 */
public class ColumnMappingPage extends ResizeComposite implements ImportPage {

    private static ColumnMatchingPanelUiBinder uiBinder = GWT
            .create(ColumnMatchingPanelUiBinder.class);


    interface ColumnMatchingPanelUiBinder extends
            UiBinder<Widget, ColumnMappingPage> {
    }


    @UiField(provided = true)
    ColumnMappingGrid dataGrid;

    @UiField(provided = true)
    FieldSelector fieldSelector;

    @UiField
    Label fieldSelectorHeading;

    @UiField
    Panel fieldSelectorPanel;

    private final ImportModel importModel;
    private final SingleSelectionModel<SourceColumn> columnSelectionModel;

    public ColumnMappingPage(ImportModel importModel, FieldChoicePresenter optionsFactory) {
        this.importModel = importModel;

        columnSelectionModel = new SingleSelectionModel<>();

        dataGrid = new ColumnMappingGrid(importModel, optionsFactory, columnSelectionModel);
        fieldSelector = new FieldSelector(optionsFactory.getOptions());

        initWidget(uiBinder.createAndBindUi(this));

        columnSelectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                onColumnChanged(columnSelectionModel.getSelectedObject());
            }
        });

        fieldSelector.addValueChangeHandler(new ValueChangeHandler<ColumnTarget>() {

            @Override
            public void onValueChange(ValueChangeEvent<ColumnTarget> event) {
                updateColumnMapping(event.getValue());
            }
        });
    }

    private void onColumnChanged(SourceColumn column) {
        fieldSelectorPanel.removeStyleName(ColumnMappingStyles.INSTANCE.incomplete());
        fieldSelectorHeading.setText(column.getHeader());
        fieldSelector.setValue(importModel.getColumnBindings().get(column.getIndex()), false);
    }

    private SourceColumn getSelectedColumn() {
        return columnSelectionModel.getSelectedObject();
    }

    private void updateColumnMapping(ColumnTarget action) {
        importModel.setColumnBinding(action, getSelectedColumn().getIndex());
        dataGrid.refreshColumnStyles(getSelectedColumn().getIndex());
    }

    @Override
    public void onResize() {
        super.onResize();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean hasNextStep() {
        return getSelectedColumn().getIndex() + 1 < importModel.getSource().getColumns().size();
    }

    @Override
    public boolean hasPreviousStep() {
        return getSelectedColumn().getIndex() > 0;
    }

    @Override
    public void start() {
        dataGrid.refresh();
        if(columnSelectionModel.getSelectedSet().isEmpty() ||
                columnSelectionModel.getSelectedObject().getIndex() != 0) {
            columnSelectionModel.setSelected(importModel.getSourceColumn(0), true);
        }
        onNextPage();
    }

    @Override
    public void nextStep() {
        if(importModel.getColumnBinding(getSelectedColumn().getIndex()) != null) {
            SourceColumn nextColumn = importModel.getSourceColumn(getSelectedColumn().getIndex() + 1);
            columnSelectionModel.setSelected(nextColumn, true);
            onNextPage();

        } else {
            fieldSelectorPanel.addStyleName(ColumnMappingStyles.INSTANCE.incomplete());
        }
    }

    private void onNextPage() {
        fieldSelector.setFocus();
    }

    @Override
    public void previousStep() {
        SourceColumn prevColumn = importModel.getSourceColumn(getSelectedColumn().getIndex() - 1);
        columnSelectionModel.setSelected(prevColumn, true);
    }
}