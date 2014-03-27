package org.activityinfo.ui.client.component.report.view;

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

import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.client.state.StateProvider;
import org.activityinfo.legacy.client.type.DateUtilGWTImpl;
import org.activityinfo.legacy.shared.command.DimensionType;
import org.activityinfo.legacy.shared.command.Filter;
import org.activityinfo.legacy.shared.model.SiteDTO;
import org.activityinfo.legacy.shared.reports.content.EntityCategory;
import org.activityinfo.legacy.shared.reports.content.PivotTableData;
import org.activityinfo.legacy.shared.reports.model.DateRange;
import org.activityinfo.legacy.shared.reports.model.PivotReportElement;
import org.activityinfo.legacy.shared.reports.util.DateUtil;
import org.activityinfo.ui.client.AppEvents;
import org.activityinfo.ui.client.EventBus;
import org.activityinfo.ui.client.page.common.Shutdownable;
import org.activityinfo.ui.client.page.common.toolbar.ActionListener;
import org.activityinfo.ui.client.page.common.toolbar.ActionToolBar;
import org.activityinfo.ui.client.page.common.toolbar.UIActions;
import org.activityinfo.ui.client.page.entry.SiteGridPanel;
import org.activityinfo.ui.client.page.entry.form.SiteDialogCallback;
import org.activityinfo.ui.client.page.entry.form.SiteDialogLauncher;
import org.activityinfo.ui.client.page.entry.grouping.NullGroupingModel;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

import static org.activityinfo.legacy.shared.reports.model.DateRange.intersection;

public class DrillDownEditor implements Shutdownable, ActionListener {

    private static final DateUtil DATES = new DateUtilGWTImpl();

    private Dispatcher dispatcher;
    private SiteGridPanel gridPanel;
    private Dialog dialog;
    private ActionToolBar toolBar;

    public DrillDownEditor(Dispatcher dispatcher) {

        this.dispatcher = dispatcher;

        createUi();
    }

    private void createUi() {
        if(gridPanel == null) {
            gridPanel = new SiteGridPanel(dispatcher, new DrillDownColumnModelProvider(dispatcher));

            createToolbar();
            createDialog();
        }
    }

    @Override
    public void shutdown() {
    }

    public void drillDown(PivotReportElement element, PivotTableData.Axis row, PivotTableData.Axis column) {

        createUi();

        // construct our filter from the intersection of rows and columns
        Filter filter = new Filter(filterFromAxis(row), filterFromAxis(column));

        // apply the effective filter
        final Filter effectiveFilter = new Filter(filter, element.getContent().getEffectiveFilter());

        gridPanel.load(NullGroupingModel.INSTANCE, effectiveFilter);
        dialog.show();
    }

    private Filter filterFromAxis(PivotTableData.Axis axis) {

        Filter filter = new Filter();
        while (axis != null) {
            if (axis.getDimension() != null) {
                if (axis.getDimension().getType() == DimensionType.Date) {
                    filter.setDateRange(intersection(filter.getDateRange(),
                            DATES.rangeFromCategory(axis.getCategory())));

                } else if (axis.getCategory() instanceof EntityCategory) {
                    filter.addRestriction(axis.getDimension().getType(),
                            ((EntityCategory) axis.getCategory()).getId());
                }
            }
            axis = axis.getParent();
        }
        return filter;
    }

    private void createToolbar() {

        toolBar = new ActionToolBar(this);
        toolBar.addEditButton();

        gridPanel.addSelectionChangedListener(new SelectionChangedListener<SiteDTO>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<SiteDTO> event) {
                toolBar.setActionEnabled(UIActions.EDIT, gridPanel.getSelection() != null);
            }
        });
    }
    
    private void createDialog() {
    	dialog = new Dialog();
    	dialog.setHeadingText(I18N.CONSTANTS.sites());
    	dialog.setButtons(Dialog.CLOSE);
    	dialog.setLayout(new FitLayout());
        dialog.setTopComponent(toolBar);
    	dialog.setSize(600, 500);
    	
    	gridPanel.setHeaderVisible(false);
    	gridPanel.setLayout(new FillLayout());
    	gridPanel.setBorders(false);
        dialog.add(gridPanel);


    }

    @Override
    public void onUIAction(String actionId) {
        if(UIActions.EDIT.equals(actionId)) {
            SiteDialogLauncher launcher = new SiteDialogLauncher(dispatcher);
            launcher.editSite(gridPanel.getSelection(), new SiteDialogCallback() {
                @Override
                public void onSaved(SiteDTO site) {
                    gridPanel.refresh();
                }
            });
        }
    }
}
