package org.activityinfo.ui.full.client.page.entry;

/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.activityinfo.api.client.Dispatcher;
import org.activityinfo.api.shared.adapter.CuidAdapter;
import org.activityinfo.api.shared.adapter.ResourceLocatorAdaptor;
import org.activityinfo.api.shared.command.DeleteSite;
import org.activityinfo.api.shared.command.Filter;
import org.activityinfo.api.shared.command.FilterUrlSerializer;
import org.activityinfo.api.shared.command.GetSchema;
import org.activityinfo.api.shared.command.result.VoidResult;
import org.activityinfo.api.shared.model.ActivityDTO;
import org.activityinfo.api.shared.model.SchemaDTO;
import org.activityinfo.api.shared.model.SiteDTO;
import org.activityinfo.api.shared.model.UserDatabaseDTO;
import org.activityinfo.api2.client.form.tree.AsyncFormTreeBuilder;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.reports.shared.model.DimensionType;
import org.activityinfo.ui.full.client.EventBus;
import org.activityinfo.ui.full.client.dispatch.monitor.MaskingAsyncMonitor;
import org.activityinfo.ui.full.client.i18n.I18N;
import org.activityinfo.ui.full.client.icon.IconImageBundle;
import org.activityinfo.api.client.KeyGenerator;
import org.activityinfo.ui.full.client.importer.ui.ImportDialog;
import org.activityinfo.ui.full.client.importer.ui.ImportPresenter;
import org.activityinfo.ui.full.client.page.*;
import org.activityinfo.ui.full.client.page.common.toolbar.ActionListener;
import org.activityinfo.ui.full.client.page.common.toolbar.ActionToolBar;
import org.activityinfo.ui.full.client.page.common.toolbar.UIActions;
import org.activityinfo.ui.full.client.page.entry.column.DefaultColumnModelProvider;
import org.activityinfo.ui.full.client.page.entry.form.PrintDataEntryForm;
import org.activityinfo.ui.full.client.page.entry.form.SiteDialogCallback;
import org.activityinfo.ui.full.client.page.entry.form.SiteDialogLauncher;
import org.activityinfo.ui.full.client.page.entry.grouping.GroupingComboBox;
import org.activityinfo.ui.full.client.page.entry.place.DataEntryPlace;
import org.activityinfo.ui.full.client.page.entry.place.UserFormPlace;
import org.activityinfo.ui.full.client.page.entry.sitehistory.SiteHistoryTab;
import org.activityinfo.ui.full.client.util.FeatureSwitch;

import java.util.Set;

/**
 * This is the container for the DataEntry page.
 */
public class DataEntryPage extends LayoutContainer implements Page,
        ActionListener {

    public static final PageId PAGE_ID = new PageId("data-entry");

    private final Dispatcher dispatcher;
    private final EventBus eventBus;

    private GroupingComboBox groupingComboBox;

    private FilterPane filterPane;

    private SiteGridPanel gridPanel;
    private CollapsibleTabPanel tabPanel;

    private DetailTab detailTab;

    private MonthlyReportsPanel monthlyPanel;
    private TabItem monthlyTab;

    private DataEntryPlace currentPlace = new DataEntryPlace();

    private AttachmentsTab attachmentsTab;

    private SiteHistoryTab siteHistoryTab;

    private ActionToolBar toolBar;

    @Inject
    public DataEntryPage(final EventBus eventBus, Dispatcher dispatcher) {
        this.eventBus = eventBus;
        this.dispatcher = dispatcher;

        setLayout(new BorderLayout());

        addFilterPane();
        addCenter();
    }

    private void addFilterPane() {
        filterPane = new FilterPane(dispatcher);
        BorderLayoutData filterLayout = new BorderLayoutData(LayoutRegion.WEST);
        filterLayout.setCollapsible(true);
        filterLayout.setMargins(new Margins(0, 5, 0, 0));
        filterLayout.setSplit(true);
        add(filterPane, filterLayout);

        filterPane.getSet().addValueChangeHandler(
                new ValueChangeHandler<Filter>() {

                    @Override
                    public void onValueChange(ValueChangeEvent<Filter> event) {
                        eventBus.fireEvent(new NavigationEvent(
                                NavigationHandler.NAVIGATION_REQUESTED,
                                currentPlace.copy()
                                        .setFilter(event.getValue())));
                    }
                });
    }

    private void addCenter() {
        gridPanel = new SiteGridPanel(dispatcher,
                new DefaultColumnModelProvider(dispatcher));
        gridPanel.setTopComponent(createToolBar());

        LayoutContainer center = new LayoutContainer();
        center.setLayout(new BorderLayout());

        center.add(gridPanel, new BorderLayoutData(LayoutRegion.CENTER));

        gridPanel
                .addSelectionChangedListener(new SelectionChangedListener<SiteDTO>() {

                    @Override
                    public void selectionChanged(SelectionChangedEvent<SiteDTO> se) {
                        onSiteSelected(se);
                    }
                });

        detailTab = new DetailTab(dispatcher);

        monthlyPanel = new MonthlyReportsPanel(dispatcher);
        monthlyTab = new TabItem(I18N.CONSTANTS.monthlyReports());
        monthlyTab.setLayout(new FitLayout());
        monthlyTab.add(monthlyPanel);

        attachmentsTab = new AttachmentsTab(dispatcher, eventBus);

        siteHistoryTab = new SiteHistoryTab(dispatcher);

        tabPanel = new CollapsibleTabPanel();
        tabPanel.add(detailTab);
        tabPanel.add(monthlyTab);
        tabPanel.add(attachmentsTab);
        tabPanel.add(siteHistoryTab);
        tabPanel.setSelection(detailTab);
        center.add(tabPanel, tabPanel.getBorderLayoutData());

        add(center, new BorderLayoutData(LayoutRegion.CENTER));
    }

    private ActionToolBar createToolBar() {
        toolBar = new ActionToolBar(this);

        groupingComboBox = new GroupingComboBox(dispatcher);
        groupingComboBox.withSelectionListener(new Listener<FieldEvent>() {

            @Override
            public void handleEvent(FieldEvent be) {
                eventBus.fireEvent(new NavigationEvent(
                        NavigationHandler.NAVIGATION_REQUESTED,
                        currentPlace.copy()
                                .setGrouping(groupingComboBox.getGroupingModel())));
            }
        });

        toolBar.add(new Label(I18N.CONSTANTS.grouping()));
        toolBar.add(groupingComboBox);

        toolBar.addButton(UIActions.ADD, I18N.CONSTANTS.newSite(),
                IconImageBundle.ICONS.add());
        toolBar.addButton(UIActions.EDIT, I18N.CONSTANTS.edit(),
                IconImageBundle.ICONS.edit());
        toolBar.addDeleteButton(I18N.CONSTANTS.deleteSite());

        toolBar.add(new SeparatorToolItem());

        toolBar.addImportButton();
        toolBar.addExcelExportButton();

        toolBar.addPrintButton();
        toolBar.addButton("EMBED", I18N.CONSTANTS.embed(),
                IconImageBundle.ICONS.embed());


        return toolBar;
    }

    private void onSiteSelected(final SelectionChangedEvent<SiteDTO> se) {
        if (se.getSelection().isEmpty()) {
            onNoSelection();
        } else {
            dispatcher.execute(new GetSchema(), new AsyncCallback<SchemaDTO>() {

                @Override
                public void onFailure(Throwable caught) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onSuccess(SchemaDTO schema) {
                    SiteDTO site = se.getSelectedItem();
                    ActivityDTO activity = schema.getActivityById(site
                            .getActivityId());
                    updateSelection(activity, site);
                }
            });
        }
    }

    private void updateSelection(ActivityDTO activity, SiteDTO site) {

        boolean permissionToEdit = activity.getDatabase().isAllowedToEdit(site);
        toolBar.setActionEnabled(UIActions.EDIT, permissionToEdit && !site.isLinked());
        toolBar.setActionEnabled(UIActions.DELETE, permissionToEdit && !site.isLinked());

        detailTab.setSite(site);
        attachmentsTab.setSite(site);
        if (activity.getReportingFrequency() == ActivityDTO.REPORT_MONTHLY) {
            monthlyPanel.load(site);
            monthlyPanel.setReadOnly(!permissionToEdit);
            monthlyTab.setEnabled(true);
        } else {
            monthlyTab.setEnabled(false);
            if (tabPanel.getSelectedItem() == monthlyTab) {
                tabPanel.setSelection(detailTab);
            }
        }
        siteHistoryTab.setSite(site);
    }

    private void onNoSelection() {
        toolBar.setActionEnabled(UIActions.EDIT, false);
        toolBar.setActionEnabled(UIActions.DELETE, false);
    }

    @Override
    public void shutdown() {
        // TODO Auto-generated method stub

    }

    @Override
    public PageId getPageId() {
        return PAGE_ID;
    }

    @Override
    public Object getWidget() {
        return this;
    }

    @Override
    public void requestToNavigateAway(PageState place,
                                      NavigationCallback callback) {
        callback.onDecided(true);
    }

    @Override
    public String beforeWindowCloses() {
        return null;
    }

    @Override
    public boolean navigate(PageState place) {
        currentPlace = (DataEntryPlace) place;
        if (!currentPlace.getFilter().isRestricted(DimensionType.Activity) &&
                !currentPlace.getFilter().isRestricted(DimensionType.Database)) {

            redirectToFirstActivity();
        } else {
            doNavigate();
        }
        return true;
    }

    private void redirectToFirstActivity() {
        dispatcher.execute(new GetSchema(), new AsyncCallback<SchemaDTO>() {

            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(SchemaDTO result) {
                for (UserDatabaseDTO db : result.getDatabases()) {
                    if (!db.getActivities().isEmpty()) {
                        currentPlace.getFilter().addRestriction(
                                DimensionType.Activity,
                                db.getActivities().get(0).getId());
                        doNavigate();
                        return;
                    }
                }
            }
        });
    }

    private void doNavigate() {
        Filter filter = currentPlace.getFilter();

        gridPanel.load(currentPlace.getGrouping(), filter);
        groupingComboBox.setFilter(filter);
        filterPane.getSet().applyBaseFilter(filter);

        // currently the print form only does one activity
        Set<Integer> activities = filter
                .getRestrictions(DimensionType.Activity);
        toolBar.setActionEnabled(UIActions.PRINT, activities.size() == 1);

        // also embedding is only implemented for one activity
        toolBar.setActionEnabled("EMBED", activities.size() == 1);
        toolBar.setActionEnabled("IMPORT", activities.size() == 1);


        // adding is also only enabled for one activity, but we have to
        // lookup to see whether it possible for this activity
        toolBar.setActionEnabled(UIActions.ADD, false);
        if (activities.size() == 1) {
            enableToolbarButtons(activities.iterator().next());
        }

    }

    private void enableToolbarButtons(final int activityId) {
        dispatcher.execute(new GetSchema(), new AsyncCallback<SchemaDTO>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(SchemaDTO result) {
                boolean isAllowed = result.getActivityById(activityId).getDatabase().isEditAllowed();
                toolBar.setActionEnabled(UIActions.ADD, isAllowed);
                toolBar.setActionEnabled("IMPORT", isAllowed);
            }
        });
    }


    @Override
    public void onUIAction(String actionId) {
        if (UIActions.ADD.equals(actionId)) {
            if (FeatureSwitch.isNewFormEnabled()) {
                int activityId = currentPlace.getFilter().getRestrictedCategory(
                        DimensionType.Activity);
                final Cuid siteCuid = CuidAdapter.siteField(new KeyGenerator().generateInt());
                final Cuid activityCuid = CuidAdapter.activityFormClass(activityId);
                eventBus.fireEvent(new NavigationEvent(
                        NavigationHandler.NAVIGATION_REQUESTED, new UserFormPlace(activityCuid, siteCuid)));
            } else {
                SiteDialogLauncher formHelper = new SiteDialogLauncher(dispatcher);
                formHelper.addSite(currentPlace.getFilter(),
                        new SiteDialogCallback() {

                            @Override
                            public void onSaved(SiteDTO site) {
                                gridPanel.refresh();
                            }
                        });
            }
        } else if (UIActions.EDIT.equals(actionId)) {
            final SiteDTO selection = gridPanel.getSelection();
            if (FeatureSwitch.isNewFormEnabled()) {
                eventBus.fireEvent(new NavigationEvent(
                        NavigationHandler.NAVIGATION_REQUESTED, new UserFormPlace(selection.getFormClassId(), selection.getInstanceId())));
            } else {
                SiteDialogLauncher launcher = new SiteDialogLauncher(dispatcher);
                launcher.editSite(selection,
                        new SiteDialogCallback() {

                            @Override
                            public void onSaved(SiteDTO site) {
                                gridPanel.refresh();
                            }
                        });
            }
        } else if (UIActions.DELETE.equals(actionId)) {
            MessageBox.confirm(I18N.CONSTANTS.appTitle(), I18N.MESSAGES.confirmDeleteSite(), new Listener<MessageBoxEvent>() {
                @Override
                public void handleEvent(MessageBoxEvent be) {
                    if(be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                        delete();
                    }
                }
            });

        } else if (UIActions.PRINT.equals(actionId)) {
            int activityId = currentPlace.getFilter().getRestrictedCategory(
                    DimensionType.Activity);
            PrintDataEntryForm form = new PrintDataEntryForm(dispatcher);
            form.print(activityId);

        } else if (UIActions.EXPORT.equals(actionId)) {
            Window.Location.assign(GWT.getModuleBaseURL() + "export?filter=" +
                    FilterUrlSerializer.toUrlFragment(currentPlace.getFilter()));

        } else if ("EMBED".equals(actionId)) {
            EmbedDialog dialog = new EmbedDialog(dispatcher);
            dialog.show(currentPlace);

        } else if ("IMPORT".equals(actionId)) {
            doImport();

        }

    }

    protected void doImport() {
        final int activityId = currentPlace.getFilter().getRestrictedCategory(
                DimensionType.Activity);


        final ResourceLocatorAdaptor resourceLocator = new ResourceLocatorAdaptor(dispatcher);
        AsyncFormTreeBuilder treeBuilder = new AsyncFormTreeBuilder(resourceLocator);

        treeBuilder.apply(CuidAdapter.activityFormClass(activityId), new AsyncCallback<FormTree>() {
            @Override
            public void onFailure(Throwable caught) {
                MessageBox.alert("Failure", caught.getMessage(), null);
            }

            @Override
            public void onSuccess(FormTree result) {
                ImportPresenter presenter = new ImportPresenter(
                        resourceLocator,
                        result);

                presenter.show();
            }
        });
    }

    private void delete() {
        dispatcher.execute(new DeleteSite(gridPanel.getSelection().getId()),
                new MaskingAsyncMonitor(this, I18N.CONSTANTS.deleting()),
                new AsyncCallback<VoidResult>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        // handled by monitor
                    }

                    @Override
                    public void onSuccess(VoidResult result) {
                        gridPanel.refresh();
                    }
                });
    }
}
