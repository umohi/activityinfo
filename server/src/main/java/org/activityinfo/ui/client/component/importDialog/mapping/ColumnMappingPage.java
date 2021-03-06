package org.activityinfo.ui.client.component.importDialog.mapping;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.activityinfo.core.shared.form.FormField;
import org.activityinfo.core.shared.importing.match.ColumnMappingGuesser;
import org.activityinfo.core.shared.importing.model.ColumnAction;
import org.activityinfo.core.shared.importing.model.ImportModel;
import org.activityinfo.core.shared.importing.model.MapExistingAction;
import org.activityinfo.core.shared.importing.source.SourceColumn;
import org.activityinfo.core.shared.importing.strategy.ColumnAccessor;
import org.activityinfo.core.shared.importing.strategy.ImportTarget;
import org.activityinfo.core.shared.importing.strategy.TargetSiteId;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.ui.client.component.importDialog.ImportPage;
import org.activityinfo.ui.client.component.importDialog.PageChangedEvent;
import org.activityinfo.ui.client.widget.Panel;

import java.util.List;
import java.util.Map;

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

    public static final int LABEL_LIMIT_IN_MESSAGE = 7;

    @UiField(provided = true)
    ColumnMappingGrid dataGrid;

    @UiField(provided = true)
    ColumnActionSelector actionSelector;

    @UiField
    Label fieldSelectorHeading;

    @UiField
    Panel fieldSelectorPanel;

    private final EventBus eventBus;
    private final ImportModel importModel;
    private final SingleSelectionModel<SourceColumn> columnSelectionModel;
    private final List<MapExistingAction> actions;

    public ColumnMappingPage(ImportModel importModel, List<MapExistingAction> actions, EventBus eventBus) {
        this.importModel = importModel;
        this.actions = actions;
        this.eventBus = eventBus;

        columnSelectionModel = new SingleSelectionModel<>();

        dataGrid = new ColumnMappingGrid(importModel, columnSelectionModel);
        actionSelector = new ColumnActionSelector(actions, importModel);

        initWidget(uiBinder.createAndBindUi(this));

        columnSelectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                onColumnChanged(columnSelectionModel.getSelectedObject());
            }
        });

        actionSelector.addValueChangeHandler(new ValueChangeHandler<ColumnAction>() {

            @Override
            public void onValueChange(ValueChangeEvent<ColumnAction> event) {
                updateColumnMapping(event.getValue());
            }
        });
    }

    private void onColumnChanged(SourceColumn column) {
        fieldSelectorPanel.removeStyleName(ColumnMappingStyles.INSTANCE.incomplete());
        fieldSelectorHeading.setText(column.getHeader());
        actionSelector.setValue(importModel.getColumnAction(column));
        actionSelector.updateTypeStyles(column.getGuessedType());
    }

    private SourceColumn getSelectedColumn() {
        return columnSelectionModel.getSelectedObject();
    }

    private void updateColumnMapping(ColumnAction action) {
        final SourceColumn removedColumn = importModel.setColumnBinding(action, getSelectedColumn());
        dataGrid.refreshColumnStyles(getSelectedColumn().getIndex());
        if (removedColumn != null) {
            dataGrid.refreshColumnStyles(removedColumn.getIndex());
        }
        fireStateChanged();
    }

    @Override
    public void onResize() {
        super.onResize();
    }

    @Override
    public boolean isValid() {
        return collectNotMappedMandatoryColumns(true).isEmpty();
    }

    /**
     * @param mandatory if true - then only mandatory, if false then ALL
     * @return
     */
    public List<ImportTarget> collectNotMappedMandatoryColumns(boolean mandatory) {
        List<ImportTarget> list = Lists.newArrayList();
        for (final MapExistingAction action : actions) {
            final FormField formField = action.getTarget().getFormField();
            if (mandatory && !formField.isRequired()) {
                continue;
            }

            final Map<TargetSiteId, ColumnAccessor> mappedColumns = importModel.getMappedColumns(formField.getId());
            if (mappedColumns.isEmpty()) {
                list.add(action.getTarget());
            }
        }
        return list;
    }


    @Override
    public void fireStateChanged() {
        final List<ImportTarget> importTargets = collectNotMappedMandatoryColumns(true);
        if (importTargets.isEmpty()) {
            eventBus.fireEvent(new PageChangedEvent(true, ""));
        } else {
            eventBus.fireEvent(new PageChangedEvent(false, I18N.MESSAGES.pleaseMapAllMandatoryColumns(columnLabels(importTargets))));
        }
    }

    private static String columnLabels(List<ImportTarget> importTargets) {
        String s = "";
        int size = importTargets.size();
        if (size > LABEL_LIMIT_IN_MESSAGE) { // restricted to not get very long message
            size = LABEL_LIMIT_IN_MESSAGE;
        }
        for (int i = 0; i < size; i++) {
            s += importTargets.get(i).getLabel();
            if (i < (size - 1)) {
                s += ", ";
            }
        }
        return s;
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
        ColumnMappingGuesser guesser = new ColumnMappingGuesser(importModel, collectNotMappedMandatoryColumns(false));
        guesser.guess();

        dataGrid.refresh();
        if (columnSelectionModel.getSelectedSet().isEmpty() ||
                columnSelectionModel.getSelectedObject().getIndex() != 0) {
            columnSelectionModel.setSelected(importModel.getSourceColumn(0), true);
        }
        onNextPage();

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                refreshGuessedColumns();
            }
        });
    }

    private void refreshGuessedColumns() {
        for (SourceColumn sourceColumn : importModel.getColumnActions().keySet()) {
            dataGrid.refreshColumnStyles(sourceColumn.getIndex());
        }
        actionSelector.updateStyles();
    }

    @Override
    public void nextStep() {
//        if (importModel.getColumnAction(getSelectedColumn()) != null) {
//            SourceColumn nextColumn = importModel.getSourceColumn(getSelectedColumn().getIndex() + 1);
//            columnSelectionModel.setSelected(nextColumn, true);
//            onNextPage();
//        } else {
//            fieldSelectorPanel.addStyleName(ColumnMappingStyles.INSTANCE.incomplete());
//        }
    }

    private void onNextPage() {
        actionSelector.setFocus();
    }

    @Override
    public void previousStep() {
//        SourceColumn prevColumn = importModel.getSourceColumn(getSelectedColumn().getIndex() - 1);
//        columnSelectionModel.setSelected(prevColumn, true);
    }
}