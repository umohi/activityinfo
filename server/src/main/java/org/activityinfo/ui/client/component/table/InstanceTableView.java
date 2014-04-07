package org.activityinfo.ui.client.component.table;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
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
import org.activityinfo.ui.client.pageView.formClass.TablePresenter;
import org.activityinfo.ui.client.style.ElementStyle;
import org.activityinfo.ui.client.widget.AlertPanel;
import org.activityinfo.ui.client.widget.ConfirmDialog;

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
    public static final int FALLBACK_TOOLBAR_HEIGHT = 24;
    public static final int HEIGHT_RECALCULATION_DELAY_MS = 500; // give client chance resize widgets

    private final ResourceLocator resourceLocator;
    private final HTMLPanel panel;
    private List<FieldColumn> columns;
    private List<FieldColumn> selectedColumns;
    private Collection<FormClass> rootFormClasses;

    @UiField
    DivElement emRuler;
    @UiField
    AlertPanel columnAlert;
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
    @UiField
    AlertPanel errorMessages;
    @UiField
    DivElement toolbar;

    interface InstanceTableViewUiBinder extends UiBinder<HTMLPanel, InstanceTableView> {
    }

    private static InstanceTableViewUiBinder ourUiBinder = GWT.create(InstanceTableViewUiBinder.class);

    public InstanceTableView(ResourceLocator resourceLocator, TablePresenter tablePresenter) {
        InstanceTableStyle.INSTANCE.ensureInjected();
        this.resourceLocator = resourceLocator;
        this.table = new InstanceTable(resourceLocator, tablePresenter);

        final AlertPanel.VisibilityHandler visibilityHandler = new AlertPanel.VisibilityHandler() {
            @Override
            public void onVisibilityChange(boolean isVisible) {
                recalculateTableHeightReduction();
            }
        };
        this.panel = ourUiBinder.createAndBindUi(this);
        this.columnAlert.addVisibilityHandler(visibilityHandler);
        this.errorMessages.addVisibilityHandler(visibilityHandler);
        initButtons();

        delayedRecalculationOfTableHeightReduction();
        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                delayedRecalculationOfTableHeightReduction();
            }
        });
    }

    private void delayedRecalculationOfTableHeightReduction() {
        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                recalculateTableHeightReduction();
                return false;
            }
        }, HEIGHT_RECALCULATION_DELAY_MS);
    }

    private void recalculateTableHeightReduction() {
        int toolbarHeight = toolbar.getOffsetHeight();
        toolbarHeight = toolbarHeight > 0 ? toolbarHeight : FALLBACK_TOOLBAR_HEIGHT; // fallback
        final int columnAlertHeight = columnAlert.getOffsetHeight();
        final int errorMessagesHeight = errorMessages.getOffsetHeight();
        table.getHeightAdjuster().recalculateTableHeight(toolbarHeight + columnAlertHeight + errorMessagesHeight);
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
            columnAlert.showMessages(I18N.CONSTANTS.notAllColumnsAreShown());
        } else {
            columnAlert.setVisible(false);
        }
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
        final String title = I18N.CONSTANTS.confirmDeletion();
        final String message = I18N.MESSAGES.removeTableRowsConfirmation(selectedSet.size(), getFormClassLabel());
        final ConfirmDialog confirmDialog = new ConfirmDialog(title, message, ElementStyle.DANGER, new ConfirmDialog.ListenerAdapter() {
            @Override
            public void onYes() {
                removeRows(selectedSet);
            }
        });
        confirmDialog.getOkButton().setText(I18N.CONSTANTS.delete());
        confirmDialog.show();
    }

    public void removeRows(Set<Projection> selectedRows) {
        final List<Cuid> cuids = Lists.newArrayList();
        for (Projection projection : selectedRows) {
            cuids.add(projection.getRootInstanceId());
        }
        resourceLocator.remove(cuids).then(new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                LOGGER.log(Level.FINE, "Failed to remove instances.", caught);
                errorMessages.showMessages(Lists.newArrayList(I18N.CONSTANTS.failedToRemoveRows()));
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