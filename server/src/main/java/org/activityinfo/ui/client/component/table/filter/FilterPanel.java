package org.activityinfo.ui.client.component.table.filter;
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.core.shared.criteria.HasCriteria;
import org.activityinfo.ui.client.component.table.FieldColumn;
import org.activityinfo.ui.client.component.table.InstanceTable;

/**
 * @author yuriyz on 4/3/14.
 */
public class FilterPanel extends Composite implements HasCriteria {

    interface FilterPanelUiBinder extends UiBinder<HTMLPanel, FilterPanel> {
    }

    private static FilterPanelUiBinder uiBinder = GWT.create(FilterPanelUiBinder.class);

    private final InstanceTable table;
    private final FieldColumn column;
    private final FilterContent filterContent;

    @UiField
    PopupPanel popup;
    @UiField
    HTMLPanel contentContainer;

    public FilterPanel(InstanceTable table, FieldColumn column) {
        this.table = table;
        this.column = column;
        initWidget(uiBinder.createAndBindUi(this));
        filterContent = FilterContentFactory.create(table, column);
        if (filterContent != null) { // we may have null for unsupported types
            contentContainer.add(filterContent);
        }
    }

    @Override
    public Criteria getCriteria() {
        return filterContent.getCriteria();
    }

    public void show(final PopupPanel.PositionCallback positionCallback) {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                popup.setPopupPositionAndShow(positionCallback);
            }
        });
    }

    public PopupPanel getPopup() {
        return popup;
    }

    @UiHandler("clearButton")
    public void onClear(ClickEvent event) {
        column.setCriteria(null);
        table.getTable().redrawHeaders();
        table.reload();
        popup.hide();
    }

    @UiHandler("okButton")
    public void onOk(ClickEvent event) {
        column.setCriteria(getCriteria());
        table.getTable().redrawHeaders();
        table.reload();
        popup.hide();
    }

    @UiHandler("cancelButton")
    public void cancelButton(ClickEvent event) {
        popup.hide();
    }
}
