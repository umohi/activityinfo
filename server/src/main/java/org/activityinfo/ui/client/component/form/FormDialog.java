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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.ui.client.component.form.field.FormFieldWidgetFactory;
import org.activityinfo.ui.client.component.form.model.FormViewModel;
import org.activityinfo.ui.client.component.form.model.FormViewModelProvider;
import org.activityinfo.ui.client.style.ModalStylesheet;
import org.activityinfo.ui.client.widget.LoadingPanel;
import org.activityinfo.ui.client.widget.ModalDialog;
import org.activityinfo.ui.client.widget.loading.ExceptionOracle;
import org.activityinfo.ui.client.widget.loading.PageLoadingPanel;

/**
 * @author yuriyz on 3/28/14.
 */
public class FormDialog {

    private FormDialogCallback callback;

    private final ResourceLocator resourceLocator;

    private final ModalDialog dialog;
    private final SimpleFormPanel formPanel;
    private final LoadingPanel<FormViewModel> loadingPanel;

    public FormDialog(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;

        ModalStylesheet.INSTANCE.ensureInjected();


        formPanel = new SimpleFormPanel(
                new VerticalFieldContainer.Factory(),
                new FormFieldWidgetFactory(resourceLocator));


        loadingPanel = new LoadingPanel<>(new PageLoadingPanel());
        loadingPanel.setDisplayWidget(formPanel);

        dialog = new ModalDialog(loadingPanel);
        dialog.getOkButton().setText(I18N.CONSTANTS.save());
        dialog.getOkButton().setStyleName("btn btn-primary");
        dialog.getOkButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                save();
            }
        });
    }

    public void setDialogTitle(String text) {
        dialog.setDialogTitle(text);
    }

    public void show(final Cuid classId, final Cuid instanceId, FormDialogCallback callback) {
        this.callback = callback;
        loadingPanel.show(new FormViewModelProvider(resourceLocator, classId, instanceId));
        dialog.show();
    }

    public void save() {
        dialog.getStatusLabel().setText(I18N.CONSTANTS.saving());
        dialog.getOkButton().setEnabled(false);
        resourceLocator.persist(formPanel.getInstance()).then(new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                dialog.getStatusLabel().setText(ExceptionOracle.getExplanation(caught));
                        dialog.getOkButton().setEnabled(true);
            }

            @Override
            public void onSuccess(Void result) {
                dialog.hide();
                callback.onPersisted(formPanel.getInstance());
            }
        });
    }

}
