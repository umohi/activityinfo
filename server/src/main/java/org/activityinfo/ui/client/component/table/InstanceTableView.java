package org.activityinfo.ui.client.component.table;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.SelectionChangeEvent;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.ui.client.component.form.FormPanelDialog;
import org.activityinfo.ui.client.component.table.dialog.VisibleColumnsDialog;
import org.activityinfo.ui.client.util.GwtUtil;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Displays the this classes' FormInstances in a table format
 */
public class InstanceTableView implements IsWidget, RequiresResize {

    private static final int DEFAULT_MAX_COLUMN_COUNT = 5;
    private static final Logger LOGGER = Logger.getLogger(InstanceTableView.class.getName());

    private final ResourceLocator resourceLocator;
    private final HTMLPanel panel;
    private List<FieldColumn> columns;
    private List<FieldColumn> selectedColumns;
    private Collection<FormClass> rootFormClasses;

    @UiField
    DivElement emRuler;
    @UiField
    DivElement columnAlert;
    @UiField(provided = true)
    InstanceTable table;
    @UiField
    Button addButton;
    @UiField
    Button removeButton;
    @UiField
    Button blukEditButton;
    @UiField
    Button editButton;

    interface InstanceTableViewUiBinder extends UiBinder<HTMLPanel, InstanceTableView> {
    }

    private static InstanceTableViewUiBinder ourUiBinder = GWT.create(InstanceTableViewUiBinder.class);

    public InstanceTableView(ResourceLocator resourceLocator) {
        InstanceTableStyle.INSTANCE.ensureInjected();
        this.resourceLocator = resourceLocator;
        this.table = new InstanceTable(resourceLocator);
        this.panel = ourUiBinder.createAndBindUi(this);

        initButtons();
    }

    private void initButtons() {
        setEditButtonState();
        setRemoveButtonState();
        table.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                setEditButtonState();
                setRemoveButtonState();
            }
        });
    }

    private void setEditButtonState() {
        final Set<Projection> selectedSet = table.getSelectionModel().getSelectedSet();
        editButton.setEnabled(!selectedSet.isEmpty() && selectedSet.size() == 1);
    }

    private void setRemoveButtonState() {
        removeButton.setEnabled(!table.getSelectionModel().getSelectedSet().isEmpty());
    }

    public void setCriteria(Criteria criteria) {
        table.setCriteria(criteria);
    }

    public void setColumns(final List<FieldColumn> columns) {
        this.columns = columns;
        Scheduler.get().scheduleDeferred(new Command() {
            @Override
            public void execute() {
                calculateSelectedColumns();
            }
        });
    }

    public void setSelectedColumns(final List<FieldColumn> selectedColumns) {
        this.selectedColumns = selectedColumns;
        table.setColumns(selectedColumns);
        if (selectedColumns.size() < columns.size()) {
            GwtUtil.setVisible(true, columnAlert);
            columnAlert.getStyle().setDisplay(Style.Display.BLOCK);
        } else {
            GwtUtil.setVisible(false, columnAlert);
        }
    }

    @UiHandler("closeAlertButton")
    public void onCloseAlert(ClickEvent event) {
        GwtUtil.setVisible(false, columnAlert);
    }

    private void calculateSelectedColumns() {
        if (columns.size() <= getMaxNumberOfColumns()) {
            setSelectedColumns(Lists.newArrayList(columns));
        } else {
            setSelectedColumns(Lists.newArrayList(columns.subList(0, getMaxNumberOfColumns())));
        }
    }

    public int getMaxNumberOfColumns() {
        double emSizeInPixels = ((double) emRuler.getOffsetWidth()) / 100d;

        LOGGER.log(Level.FINE, "emSizeInPixels = " + emSizeInPixels);

        double columnWidthInPixels = InstanceTable.COLUMN_WIDTH * emSizeInPixels;

        int columnLimit = (int) Math.floor(panel.getElement().getClientWidth() / columnWidthInPixels);
        LOGGER.log(Level.FINE, "columnLimit = " + columnLimit);
        if (columnLimit <= 0) { // fallback : yuriyz: check calculations above
            columnLimit = DEFAULT_MAX_COLUMN_COUNT;
        }
        return columnLimit;
    }

    public InstanceTable getTable() {
        return table;
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    public void onResize() {

    }

    @UiHandler("visibleColumns")
    public void onConfigure(ClickEvent event) {
        final VisibleColumnsDialog visibleColumnsDialog = new VisibleColumnsDialog(this);
        visibleColumnsDialog.show();
    }

    @UiHandler("addButton")
    public void onAdd(ClickEvent event) {
        final FormClass formClass = rootFormClasses.iterator().next();
        final Cuid instanceId = CuidAdapter.newFormInstance(formClass.getId());
        final FormPanelDialog dialog = new FormPanelDialog(resourceLocator) {
            @Override
            public void onPersistedSuccessfully() {
                getTable().reload();
            }
        };
        dialog.setDialogTitle(I18N.CONSTANTS.add() + " " + getFormClassLabel());
        dialog.show(formClass.getId(), instanceId);

        // navigate to new page
//        final UserFormPlace userFormPlace = new UserFormPlace(formClass.getId(), instanceId);
//        History.newItem(userFormPlace.serializeAsPlaceHistoryToken());
    }

    private String getFormClassLabel() {
        if (rootFormClasses != null && !rootFormClasses.isEmpty()) {
            final FormClass formClass = rootFormClasses.iterator().next();
            return formClass.getLabel().getValue();
        }
        return "";
    }

    @UiHandler("editButton")
    public void onEdit(ClickEvent event) {
        final Projection selectedProjection = table.getSelectionModel().getSelectedSet().iterator().next();
        final FormPanelDialog dialog = new FormPanelDialog(resourceLocator) {
            @Override
            public void onPersistedSuccessfully() {
                getTable().reload();
            }
        };
        dialog.setDialogTitle(I18N.CONSTANTS.edit() + " " + getFormClassLabel());
        dialog.show(selectedProjection.getRootClassId(), selectedProjection.getRootInstanceId());

        // navigate to new page
//        final UserFormPlace userFormPlace = new UserFormPlace(selectedProjection.getRootClassId(), selectedProjection.getRootInstanceId());
//        History.newItem(userFormPlace.serializeAsPlaceHistoryToken());
    }

    @UiHandler("removeButton")
    public void onRemove(ClickEvent event) {
        final Set<Projection> selectedSet = table.getSelectionModel().getSelectedSet();
        final List<Cuid> cuids = Lists.newArrayList();
        for (Projection projection: selectedSet) {
            cuids.add(projection.getRootInstanceId());
        }
        resourceLocator.remove(cuids).then(new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                LOGGER.log(Level.FINE, "Failed to remove instances.");
            }

            @Override
            public void onSuccess(Void result) {
                table.reload();
            }
        });
    }

    public List<FieldColumn> getColumns() {
        if (columns == null) {
            columns = Lists.newArrayList();
        }
        return columns;
    }

    public List<FieldColumn> getSelectedColumns() {
        if (selectedColumns == null) {
            selectedColumns = Lists.newArrayList();
        }
        return selectedColumns;
    }

    public ResourceLocator getResourceLocator() {
        return resourceLocator;
    }

    public void setRootFormClasses(Collection<FormClass> rootFormClasses) {
        this.rootFormClasses = rootFormClasses;
    }
}