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
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.form.FormClass;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.ui.full.client.Log;
import org.activityinfo.ui.full.client.i18n.I18N;
import org.activityinfo.ui.full.client.page.NavigationCallback;
import org.activityinfo.ui.full.client.page.Page;
import org.activityinfo.ui.full.client.page.PageId;
import org.activityinfo.ui.full.client.page.PageState;
import org.activityinfo.ui.full.client.page.entry.place.UserFormPlace;
import org.activityinfo.ui.full.client.page.entry.place.UserFormPlaceParser;
import org.activityinfo.ui.full.client.style.TransitionUtil;
import org.activityinfo.ui.full.client.widget.form.UserFormPanel;

/**
 * @author yuriyz on 1/31/14.
 */
public class UserFormPage extends Composite implements Page {

    private static SiteFormPageUiBinder uiBinder = GWT
            .create(SiteFormPageUiBinder.class);

    public static interface SiteFormPageUiBinder extends UiBinder<Widget, UserFormPage> {
    }

    private final ResourceLocator resourceLocator;
    private final UserFormPanel userFormPanel;
    private final UserFormPlace userFormPlace;

    @UiField
    FlowPanel panel;

    @Inject
    public UserFormPage(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;

        TransitionUtil.ensureBootstrapInjected();
        initWidget(uiBinder.createAndBindUi(this));

        userFormPlace = UserFormPlaceParser.parseToken(History.getToken());
        userFormPanel = new UserFormPanel();
        panel.add(userFormPanel);

        init();
    }

    private void init() {
//        if (userFormPlace.getUserFormType() != null) { // create new
//            resourceLocator.createUserForm().then(new AsyncCallback<FormClass>() {
//                @Override
//                public void onFailure(Throwable caught) {
//                    Log.error("Unable to create user form.", caught);
//                    userFormPanel.showError(I18N.CONSTANTS.errorUnexpectedOccured());
//                }
//
//                @Override
//                public void onSuccess(FormClass result) {
//                    userFormPanel.renderForm(result);
//                    resourceLocator.createFormInstance(new FormInstance(null, userFormPanel.getFormClass().getId())).then(new AsyncCallback<Iri>() {
//                        @Override
//                        public void onFailure(Throwable caught) {
//                            Log.error("Unable to create user form instance", caught);
//                            userFormPanel.showError(I18N.CONSTANTS.errorUnexpectedOccured());
//                        }
//
//                        @Override
//                        public void onSuccess(Iri result) {
//                            userFormPanel.setValue(new FormInstance(result, userFormPanel.getFormClass().getId()));
//                        }
//                    });
//                }
//            });
//        } else if (userFormPlace.getUserFormId() != null) { // edit
//            fetchRemote();
//        } else {
//            Log.error("Unable to identify whether to CREATE or EDIT user form, token: " + History.getToken());
//        }

        userFormPanel.addHandler(new UserFormPanel.Handler() {
            @Override
            public void onSave() {
                UserFormPage.this.onSave();
            }
        });
    }

    private void onSave() {
        final FormInstance value = userFormPanel.getValue();
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

    private UserFormPlace fetchRemote() {
        resourceLocator.getUserForm(userFormPlace.getUserFormId()).fetch().then(new AsyncCallback<FormClass>() {
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Unable to fetch UserForm, iri=" + userFormPlace.getUserFormId(), caught);
            }

            @Override
            public void onSuccess(FormClass result) {
                userFormPanel.renderForm(result);
                resourceLocator.getFormInstance(userFormPlace.getUserFormInstanceId()).fetch().then(new AsyncCallback<FormInstance>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Log.error("Unable to fetch FormInstance, iri=" + userFormPlace.getUserFormInstanceId(), caught);
                    }

                    @Override
                    public void onSuccess(FormInstance result) {
                        userFormPanel.setValue(result);
                    }
                });
            }
        });
        return userFormPlace;
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
