package org.activityinfo.ui.full.client.widget.form;
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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.api.shared.adapter.ActivityAdapter;
import org.activityinfo.api.shared.model.ActivityDTO;
import org.activityinfo.api.shared.model.LocationDTO;
import org.activityinfo.api.shared.model.SiteDTO;
import org.activityinfo.api2.shared.form.UserForm;
import org.activityinfo.ui.full.client.dispatch.Dispatcher;
import org.activityinfo.ui.full.client.page.entry.form.SiteDialogCallback;
import org.activityinfo.ui.full.client.style.TransitionUtil;

/**
 * @author yuriyz on 1/27/14.
 */
public class SiteDialog {

    private static SiteDialogUiBinder uiBinder = GWT
            .create(SiteDialogUiBinder.class);

    interface SiteDialogUiBinder extends UiBinder<Widget, SiteDialog> {
    }

    private final PopupPanel popupPanel = new PopupPanel(true);

    @UiField
    HTMLPanel contentPanel;

    public SiteDialog(UserForm siteForm) {
        TransitionUtil.ensureBootstrapInjected();

        final UserFormPanel userFormPanel = new UserFormPanel(siteForm, null);
        final Widget content = uiBinder.createAndBindUi(this);

        contentPanel.add(userFormPanel);
        popupPanel.add(content);
    }

    public SiteDialog(Dispatcher dispatcher, ActivityDTO activity) {
        this(new ActivityAdapter(activity).getSiteForm());
    }

    public void showNew(SiteDTO newSite, LocationDTO location, boolean isNew, SiteDialogCallback callback) {
        show();
    }

    public void show() {
        popupPanel.center();
    }

    @UiHandler("closeButton")
    public void onClose(ClickEvent event) {
        popupPanel.hide();
    }
}
