package org.activityinfo.ui.client.component.report.editor.map.layerOptions;

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

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.client.callback.SuccessCallback;
import org.activityinfo.legacy.shared.command.DimensionType;
import org.activityinfo.legacy.shared.command.Filter;
import org.activityinfo.ui.client.component.filter.AttributeGroupFilterWidget;
import org.activityinfo.ui.client.component.filter.AttributeGroupFilterWidgets;
import org.activityinfo.ui.client.component.filter.FilterPanelSet;
import org.activityinfo.ui.client.component.filter.FilterResources;
import org.activityinfo.ui.client.style.legacy.icon.IconImageBundle;

public class LayerFilterPanel extends ContentPanel implements HasValue<Filter> {

    private FilterPanelSet filterPanelSet;
    private Filter filter;
    private DateFilterWidget dateWidget;
    private PartnerFilterWidget partnerFilterWidget;
    private AttributeGroupFilterWidgets attributeGroupWidgets;

    public LayerFilterPanel(Dispatcher dispatcher) {
        FilterResources.INSTANCE.style().ensureInjected();

        initializeComponent();

        dateWidget = new DateFilterWidget();
        dateWidget.addValueChangeHandler(new ValueChangeHandler<Filter>() {
            @Override
            public void onValueChange(ValueChangeEvent<Filter> event) {
                createNewFilterAndFireEvent();
            }
        });
        add(dateWidget);

        partnerFilterWidget = new PartnerFilterWidget(dispatcher);
        partnerFilterWidget
                .addValueChangeHandler(new ValueChangeHandler<Filter>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Filter> event) {
                        createNewFilterAndFireEvent();
                    }
                });
        add(partnerFilterWidget);

        attributeGroupWidgets = new AttributeGroupFilterWidgets(this, dispatcher,
                new ValueChangeHandler<Filter>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Filter> event) {
                        createNewFilterAndFireEvent();
                    }
                },
                new SuccessCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        layout();
                    }
                }
        );

        filterPanelSet = new FilterPanelSet(dateWidget, partnerFilterWidget, attributeGroupWidgets);
    }

    private void createNewFilterAndFireEvent() {
        Filter filter = new Filter();

        filter.setDateRange(dateWidget.getValue().getDateRange());

        Filter partnerFilter = partnerFilterWidget.getValue();
        if (partnerFilter.hasRestrictions()) {
            filter.addRestriction(DimensionType.Partner,
                    partnerFilter.getRestrictions(DimensionType.Partner));
        }

        Filter attributeGroupFilter = attributeGroupWidgets.getValue();
        if (attributeGroupFilter.hasRestrictions()) {
            filter.addRestriction(AttributeGroupFilterWidget.DIMENSION_TYPE,
                    attributeGroupFilter.getRestrictions(AttributeGroupFilterWidget.DIMENSION_TYPE));
        }

        setValue(filter);
    }

    private void initializeComponent() {
        setHeadingText(I18N.CONSTANTS.filter());
        setIcon(IconImageBundle.ICONS.filter());
    }

    public FilterPanelSet getFilterPanelSet() {
        return filterPanelSet;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(
            ValueChangeHandler<Filter> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public Filter getValue() {
        return filter;
    }

    @Override
    public void setValue(Filter value) {
        setValue(value, true);
    }

    @Override
    public void setValue(Filter value, boolean fireEvents) {
        if (value == null) {
            value = new Filter();
        } else {
            this.filter = value;
            dateWidget.setValue(value, false);
            partnerFilterWidget.setValue(value, false);
        }
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

}
