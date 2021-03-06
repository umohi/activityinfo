package org.activityinfo.ui.client.component.report.editor.map.mapOptions;

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

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.client.monitor.MaskingAsyncMonitor;
import org.activityinfo.legacy.shared.command.GetBaseMaps;
import org.activityinfo.legacy.shared.command.result.BaseMapResult;
import org.activityinfo.legacy.shared.model.BaseMap;
import org.activityinfo.legacy.shared.model.TileBaseMap;
import org.activityinfo.legacy.shared.reports.content.GoogleBaseMap;
import org.activityinfo.ui.client.component.report.editor.map.MapResources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Displays a list of options and hints for the map
 */
public class BaseMapDialog extends Dialog {
    private final Dispatcher service;
    private ListView<ModelData> listView;
    private String originalId;

    public interface Callback {
        void onSelect(String baseMapId, String label);
    }

    private Callback callback;

    public BaseMapDialog(Dispatcher service) {
        this.service = service;

        initializeComponent();
        createListView();
        loadBaseMaps();

        MapResources.INSTANCE.baseMapDialogStyle().ensureInjected();

        setButtons(OKCANCEL);
    }

    public void show(String selectedId, Callback callback) {
        super.show();
        this.callback = callback;
        this.originalId = selectedId;
    }

    private void initializeComponent() {
        setWidth(675);
        setHeight(400);
        setHeadingText(I18N.CONSTANTS.basemap());
        setLayout(new FitLayout());
        addStyleName("basemap-dlg");
        setBodyBorder(false);
    }

    private void createListView() {
        listView = new ListView<ModelData>();
        listView.setTemplate(getTemplate());
        listView.setStore(new ListStore<ModelData>());
        listView.setItemSelector("div.thumb-wrap");
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        add(listView);
    }

    private native String getTemplate() /*-{
        return ['<tpl for=".">',
            '<div class="thumb-wrap">',
            '<div class="thumb"><img src="{path}" title="{name}"></div>',
            '<span class="x-editable">{name}</span></div>',
            '</tpl>',
            '<div class="x-clear"></div>'].join("");
    }-*/;

    private void loadBaseMaps() {
        listView.getStore().removeAll();
        listView.getStore().add(
                googleThumb(GoogleBaseMap.ROADMAP, I18N.CONSTANTS.googleRoadmap()));
        listView.getStore().add(
                googleThumb(GoogleBaseMap.SATELLITE,
                        I18N.CONSTANTS.googleSatelliteMap()));
        listView.getStore().add(
                googleThumb(GoogleBaseMap.HYBRID, I18N.CONSTANTS.googleHybrid()));
        listView.getStore().add(
                googleThumb(GoogleBaseMap.TERRAIN,
                        I18N.CONSTANTS.googleTerrainMap()));
        updateSelection();

        service.execute(new GetBaseMaps(),
                new MaskingAsyncMonitor(listView, I18N.CONSTANTS.loading()),
                new AsyncCallback<BaseMapResult>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        failLoadingBaseMaps();
                    }

                    @Override
                    public void onSuccess(BaseMapResult result) {
                        List<ModelData> thumbnails = new ArrayList<ModelData>();
                        for (BaseMap baseMap : result.getBaseMaps()) {
                            thumbnails.add(tileThumb(baseMap));
                        }
                        listView.getStore().add(thumbnails);
                        updateSelection();
                    }
                });
    }

    private void updateSelection() {
        ModelData selected = listView.getStore().findModel("id", originalId);
        if (selected != null) {
            listView.getSelectionModel().setSelection(Arrays.asList(selected));
        }
    }

    private void failLoadingBaseMaps() {
        Label labelFailLoading = new Label(I18N.CONSTANTS.failBaseMapLoading());
        add(labelFailLoading);
    }

    private ModelData googleThumb(GoogleBaseMap baseMap, String name) {
        BaseModelData thumb = new BaseModelData();
        thumb.set("id", baseMap.getId());
        thumb.set("name", name);
        thumb.set("path",
                GWT.getModuleBaseURL() + "basemaps/" + baseMap.getId() + ".png");

        return thumb;
    }

    private ModelData tileThumb(BaseMap baseMap) {
        BaseModelData thumb = new BaseModelData();
        thumb.set("id", baseMap.getId());
        thumb.set("name", ((TileBaseMap) baseMap).getName());
        thumb.set("path", ((TileBaseMap) baseMap).getThumbnailUrl());
        return thumb;
    }

    @Override
    protected void onButtonPressed(Button button) {
        if (button.getItemId().equals("cancel")) {
            hide();
        } else {
            ModelData thumb = listView.getSelectionModel().getSelectedItem();
            if (thumb != null) {
                callback.onSelect((String) thumb.get("id"),
                        (String) thumb.get("name"));
                hide();
            }
        }
    }
}
