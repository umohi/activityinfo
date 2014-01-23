package org.activityinfo.ui.full.client.page.dashboard.portlets;

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

import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.analysis.shared.content.Content;
import org.activityinfo.analysis.shared.model.*;
import org.activityinfo.api.shared.command.GenerateElement;
import org.activityinfo.api.shared.command.GetReportModel;
import org.activityinfo.api.shared.command.UpdateReportSubscription;
import org.activityinfo.api.shared.command.result.VoidResult;
import org.activityinfo.api.shared.model.ReportDTO;
import org.activityinfo.api.shared.model.ReportMetadataDTO;
import org.activityinfo.ui.full.client.EventBus;
import org.activityinfo.ui.full.client.dispatch.Dispatcher;
import org.activityinfo.ui.full.client.i18n.I18N;
import org.activityinfo.ui.full.client.icon.IconImageBundle;
import org.activityinfo.ui.full.client.page.NavigationEvent;
import org.activityinfo.ui.full.client.page.NavigationHandler;
import org.activityinfo.ui.full.client.page.report.ReportDesignPageState;
import org.activityinfo.ui.full.client.report.view.ChartOFCView;
import org.activityinfo.ui.full.client.report.view.MapReportView;
import org.activityinfo.ui.full.client.report.view.PivotGridPanel;
import org.activityinfo.ui.full.client.report.view.ReportView;

public class ReportPortlet extends Portlet {

    private final Dispatcher dispatcher;
    private final ReportMetadataDTO metadata;
    private final EventBus eventBus;

    public ReportPortlet(Dispatcher dispatcher, EventBus eventBus,
                         ReportMetadataDTO report) {
        this.dispatcher = dispatcher;
        this.eventBus = eventBus;
        this.metadata = report;

        setHeadingText(report.getTitle());
        setHeight(275);
        setLayout(new FitLayout());

        addOptionsMenu();
        addCloseButton();

        add(new Label(I18N.CONSTANTS.loading()));

        loadModel();
    }

    private void addOptionsMenu() {
        final Menu optionsMenu = new Menu();

        optionsMenu.add(new MenuItem(I18N.CONSTANTS.edit(),
                IconImageBundle.ICONS.edit(), new SelectionListener<MenuEvent>() {

            @Override
            public void componentSelected(MenuEvent ce) {
                edit();
            }
        }));

        optionsMenu.add(new MenuItem(I18N.CONSTANTS.removeFromDashboard(),
                IconImageBundle.ICONS.remove(), new SelectionListener<MenuEvent>() {

            @Override
            public void componentSelected(MenuEvent ce) {
                removeFromDashboard();
            }
        }));

        final ToolButton gear = new ToolButton("x-tool-gear",
                new SelectionListener<IconButtonEvent>() {

                    @Override
                    public void componentSelected(IconButtonEvent ce) {
                        optionsMenu.show(ce.getComponent());
                    }
                });
        getHeader().addTool(gear);
    }

    private void addCloseButton() {
        getHeader().addTool(
                new ToolButton("x-tool-close",
                        new SelectionListener<IconButtonEvent>() {

                            @Override
                            public void componentSelected(IconButtonEvent ce) {
                                removeFromDashboard();
                            }
                        }));

        getHeader().addTool(
                new ToolButton("x-tool-maximize",
                        new SelectionListener<IconButtonEvent>() {

                            @Override
                            public void componentSelected(IconButtonEvent ce) {
                                edit();
                            }
                        }));
    }

    private void loadModel() {
        dispatcher.execute(new GetReportModel(metadata.getId()),
                new AsyncCallback<ReportDTO>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onSuccess(ReportDTO dto) {
                        onModelLoad(dto);
                    }
                });
    }

    private void onModelLoad(ReportDTO dto) {
        Report report = dto.getReport();
        if (report.getElements().isEmpty()) {
            removeAll();
            add(new Label("The report is empty"));
            return;
        }
        final ReportElement element = report.getElement(0);
        final ReportView view = createView(element);

        if (view == null) {
            removeAll();
            add(new Label("Unsupport report type"));
            layout();
            return;
        }

        dispatcher.execute(new GenerateElement<Content>(element),
                new AsyncCallback<Content>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onSuccess(Content result) {
                        element.setContent(result);
                        view.show(element);
                        removeAll();
                        add(view.asComponent());
                        layout();
                    }
                });
    }

    private ReportView createView(ReportElement element) {
        if (element instanceof PivotChartReportElement) {
            return new ChartOFCView();
        } else if (element instanceof PivotTableReportElement) {
            PivotGridPanel gridPanel = new PivotGridPanel();
            gridPanel.setHeaderVisible(false);
            return gridPanel;
        } else if (element instanceof MapReportElement) {
            MapReportView mapView = new MapReportView();
            return mapView;
        } else {
            return null;
        }
    }

    private void edit() {
        eventBus.fireEvent(new NavigationEvent(
                NavigationHandler.NAVIGATION_REQUESTED,
                new ReportDesignPageState(metadata.getId())));
    }

    private void removeFromDashboard() {
        MessageBox.confirm(metadata.getTitle(),
                I18N.CONSTANTS.confirmRemoveFromDashboard(),
                new Listener<MessageBoxEvent>() {

                    @Override
                    public void handleEvent(MessageBoxEvent be) {
                        if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                            UpdateReportSubscription update = new UpdateReportSubscription();
                            update.setReportId(metadata.getId());
                            update.setPinnedToDashboard(false);

                            dispatcher.execute(update,
                                    new AsyncCallback<VoidResult>() {

                                        @Override
                                        public void onFailure(Throwable caught) {
                                            // TODO Auto-generated method stub

                                        }

                                        @Override
                                        public void onSuccess(VoidResult result) {
                                            removeFromParent();
                                        }
                                    });
                        }
                    }
                });
    }
}
