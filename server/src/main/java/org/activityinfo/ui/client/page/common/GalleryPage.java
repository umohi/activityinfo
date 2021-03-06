package org.activityinfo.ui.client.page.common;

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

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.ListViewEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import org.activityinfo.ui.client.EventBus;
import org.activityinfo.ui.client.page.NavigationEvent;
import org.activityinfo.ui.client.page.NavigationHandler;
import org.activityinfo.ui.client.page.PageState;

/**
 * @author Alex Bertram (akbertram@gmail.com)
 */
public class GalleryPage extends LayoutContainer implements GalleryView {

    private ListStore<GalleryModel> store;
    private Html heading;
    private Html introPara;

    public static class GalleryModel extends BaseModelData {

        private PageState place;

        public GalleryModel(String name, String desc, String path,
                            PageState place) {
            set("name", name);
            set("path", path);
            set("desc", desc);

            this.place = place;
        }

        public PageState getPlace() {
            return place;
        }
    }

    @Inject
    public GalleryPage(final EventBus eventBus) {

        this.setStyleName("gallery");
        this.setScrollMode(Style.Scroll.AUTOY);
        this.setStyleAttribute("background", "white");

        setLayout(new FlowLayout());

        heading = new Html();
        heading.setTagName("h3");
        add(heading);

        introPara = new Html();
        introPara.setTagName("p");
        introPara.setStyleName("gallery-intro");
        add(introPara);

        store = new ListStore<GalleryModel>();

        ListView<GalleryModel> view = new ListView<GalleryModel>();
        view.setTemplate(getTemplate(GWT.getModuleBaseURL() + "image/"));
        view.setBorders(false);
        view.setStore(store);
        view.setItemSelector("dd");
        view.setOverStyle("over");

        view.addListener(Events.Select,
                new Listener<ListViewEvent<GalleryModel>>() {

                    @Override
                    public void handleEvent(ListViewEvent<GalleryModel> event) {
                        eventBus.fireEvent(new NavigationEvent(
                                NavigationHandler.NAVIGATION_REQUESTED,
                                event.getModel().getPlace()));
                    }
                });
        add(view);
    }

    @Override
    public void setHeading(String html) {
        heading.setHtml(html);
    }

    @Override
    public void setIntro(String html) {
        introPara.setHtml(html);
    }

    @Override
    public void add(String name, String desc, String path, PageState place) {

        store.add(new GalleryModel(name, desc, path, place));
    }

    private native String getTemplate(String base) /*-{
        return ['<dl><tpl for=".">',
            '<dd>',
            '<img src="' + base + 'thumbs/{path}" title="{name}">',
            '<div>',
            '<h4>{name}</h4><p>{desc}</p></div>',
            '</tpl>',
            '<div style="clear:left;"></div></dl>'].join("");

    }-*/;

}
