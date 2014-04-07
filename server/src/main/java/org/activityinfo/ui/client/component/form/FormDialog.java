package org.activityinfo.ui.client.component.form;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.ui.client.component.form.field.FormFieldWidgetFactory;
import org.activityinfo.ui.client.component.form.model.FormViewModel;
import org.activityinfo.ui.client.component.form.model.FormViewModelProvider;
import org.activityinfo.ui.client.style.ModalStylesheet;
import org.activityinfo.ui.client.widget.LoadingPanel;
import org.activityinfo.ui.client.widget.ModalTitle;
import org.activityinfo.ui.client.widget.loading.PageLoadingPanel;

/**
 * @author yuriyz on 3/28/14.
 */
public class FormDialog {

    private FormDialogCallback callback;

    interface FormDialogUiBinder extends UiBinder<PopupPanel, FormDialog> {
    }

    private static FormDialogUiBinder ourUiBinder = GWT.create(FormDialogUiBinder.class);

    private final ResourceLocator resourceLocator;

    private final PopupPanel popupPanel;
    private final SimpleFormPanel formPanel;
    private final LoadingPanel<FormViewModel> loadingPanel;

    @UiField
    ModalTitle title;

    @UiField
    ScrollPanel container;

    @UiField
    Button saveButton;

    @UiField
    Button cancelButton;

    @UiField
    InlineLabel statusLabel;

    public FormDialog(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;

        ModalStylesheet.INSTANCE.ensureInjected();


        popupPanel = ourUiBinder.createAndBindUi(this);
        popupPanel.setWidth(Math.min(500, Window.getClientWidth()) + "px");
        popupPanel.setHeight((Window.getClientHeight()-50) + "px");
        popupPanel.center();

        formPanel = new SimpleFormPanel(
                new VerticalFieldContainer.Factory(),
                new FormFieldWidgetFactory(resourceLocator));


        loadingPanel = new LoadingPanel<>(new PageLoadingPanel());
        loadingPanel.setDisplayWidget(formPanel);

        container.add(loadingPanel);
    }

    public void setDialogTitle(String text) {
        title.setText(text);
    }

    public void show(final Cuid classId, final Cuid instanceId, FormDialogCallback callback) {
        this.callback = callback;
        loadingPanel.show(new FormViewModelProvider(resourceLocator, classId, instanceId));
        popupPanel.show();
    }

    @UiHandler("saveButton")
    public void onSave(ClickEvent event) {
        statusLabel.setText(I18N.CONSTANTS.saving());
        saveButton.setEnabled(false);
        resourceLocator.persist(formPanel.getInstance()).then(new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                statusLabel.setText(I18N.CONSTANTS.connectionProblem());
                saveButton.setEnabled(true);
            }

            @Override
            public void onSuccess(Void result) {
                popupPanel.hide();
                callback.onPersisted(formPanel.getInstance());
            }
        });
    }

    @UiHandler("cancelButton")
    public void onCancel(ClickEvent event) {
        popupPanel.hide();
    }
}
