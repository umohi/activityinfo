package org.activityinfo.ui.client.page.entry;
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
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.legacy.shared.Log;
import org.activityinfo.ui.client.component.form.FormPanel;
import org.activityinfo.ui.client.page.NavigationCallback;
import org.activityinfo.ui.client.page.Page;
import org.activityinfo.ui.client.page.PageId;
import org.activityinfo.ui.client.page.PageState;
import org.activityinfo.ui.client.page.entry.place.UserFormPlace;
import org.activityinfo.ui.client.page.entry.place.UserFormPlaceParser;
import org.activityinfo.ui.client.style.BaseStylesheet;

/**
 * @author yuriyz on 1/31/14.
 */
public class FormPanelPage extends Composite implements Page {

    private static SiteFormPageUiBinder uiBinder = GWT
            .create(SiteFormPageUiBinder.class);

    public static interface SiteFormPageUiBinder extends UiBinder<Widget, FormPanelPage> {
    }

    private final ResourceLocator resourceLocator;
    private final FormPanel formPanel;
    private final UserFormPlace userFormPlace;

    @UiField
    FlowPanel panel;

    @Inject
    public FormPanelPage(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;

        BaseStylesheet.INSTANCE.ensureInjected();
        initWidget(uiBinder.createAndBindUi(this));

        userFormPlace = UserFormPlaceParser.parseToken(History.getToken());
        formPanel = new FormPanel(resourceLocator);
        formPanel.setDesignEnabled(true);
        panel.add(formPanel);

        init();
    }

    private void init() {
        fetchRemote();
        formPanel.addHandler(new FormPanel.Handler() {
            @Override
            public void onSave() {
                FormPanelPage.this.onSave();
            }
        });
    }

    private void onSave() {
        final FormInstance value = formPanel.getValue();
        if (value != null) {
            resourceLocator.persist(value).then(new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable caught) {
                    Log.error("Failed to save form instance");
                }

                @Override
                public void onSuccess(Void result) {
                }
            });
        }
    }

    private void fetchRemote() {
        resourceLocator.getFormClass(userFormPlace.getUserFormId()).then(new AsyncCallback<FormClass>() {
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Unable to fetch UserForm, iri=" + userFormPlace.getUserFormId(), caught);
            }

            @Override
            public void onSuccess(FormClass result) {
                formPanel.renderForm(result);
                resourceLocator.getFormInstance(userFormPlace.getUserFormInstanceId()).then(new AsyncCallback<FormInstance>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Log.error("Unable to fetch FormInstance, iri=" + userFormPlace.getUserFormInstanceId(), caught);
                    }

                    @Override
                    public void onSuccess(FormInstance result) {
                        if (result == null || result.getId() == null) {
                            formPanel.setValue(new FormInstance(userFormPlace.getUserFormId(), userFormPlace.getUserFormInstanceId()));
                        } else {
                            formPanel.setValue(result);
                        }
                    }
                });
            }
        });
    }

    @Override
    public Widget getWidget() {
        return this;
    }

    @Override
    public PageId getPageId() {
        return UserFormPlace.PAGE_ID;
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
