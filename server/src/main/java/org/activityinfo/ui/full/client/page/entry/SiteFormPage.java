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

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.activityinfo.ui.full.client.page.NavigationCallback;
import org.activityinfo.ui.full.client.page.Page;
import org.activityinfo.ui.full.client.page.PageId;
import org.activityinfo.ui.full.client.page.PageState;
import org.activityinfo.ui.full.client.style.TransitionUtil;
import org.activityinfo.ui.full.client.widget.form.UserFormPanel;

/**
 * @author yuriyz on 1/31/14.
 */
public class SiteFormPage extends Composite implements Page {

    private static SiteFormPageUiBinder uiBinder = GWT
            .create(SiteFormPageUiBinder.class);

    public static interface SiteFormPageUiBinder extends UiBinder<Widget, SiteFormPage> {
    }

    public static final PageId PAGE_ID = new PageId("site-form");

    @UiField
    UserFormPanel userFormPanel;

    @Inject
    public SiteFormPage() {
        TransitionUtil.ensureBootstrapInjected();
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public Widget getWidget() {
        return this;
    }

    @Override
    public PageId getPageId() {
        return PAGE_ID;
    }

    @Override
    public void requestToNavigateAway(PageState place, NavigationCallback callback) {

    }

    @Override
    public String beforeWindowCloses() {
        return null;
    }

    @Override
    public boolean navigate(PageState place) {
        return true;
    }

    @Override
    public void shutdown() {

    }
}
