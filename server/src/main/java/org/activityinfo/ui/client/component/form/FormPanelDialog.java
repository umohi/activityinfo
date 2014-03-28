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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ScrollPanel;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.legacy.shared.Log;
import org.activityinfo.ui.client.widget.ModalDialog;

/**
 * @author yuriyz on 3/28/14.
 */
public class FormPanelDialog extends ModalDialog {

    private final ResourceLocator resourceLocator;
    private final FormPanel formPanel;
    private ScrollPanel scrollPanel;

    public FormPanelDialog(ResourceLocator resourceLocator) {
        super();
        getDialog().show();
        this.resourceLocator = resourceLocator;
        getDialogDiv().setAttribute("style", "width:auto;");
        formPanel = new FormPanel(resourceLocator);
        formPanel.setDesignEnabled(false);
    }

//    public FormPanelDialog(ResourceLocator resourceLocator, Cuid classId, Cuid instanceId) {
//        this(resourceLocator);
//        load(classId, instanceId);
//    }

    public FormPanel getFormPanel() {
        return formPanel;
    }

    public void show(final Cuid classId, final Cuid instanceId) {
        scrollPanel = new ScrollPanel();
        scrollPanel.setHeight("500px");
        scrollPanel.setWidth("800px");
        getModalBody().setWidth("850px");
        getModalBody().add(scrollPanel);
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                getDialog().center();
                load(classId, instanceId);
            }
        });
    }

    private void load(final Cuid classId, final Cuid instanceId) {
        resourceLocator.getFormClass(classId).then(new AsyncCallback<FormClass>() {
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Unable to fetch UserForm, iri=" + classId, caught);
            }

            @Override
            public void onSuccess(FormClass result) {
                scrollPanel.add(formPanel);
                formPanel.renderForm(result);
                resourceLocator.getFormInstance(instanceId).then(new AsyncCallback<FormInstance>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Log.error("Unable to fetch FormInstance, iri=" + instanceId, caught);
                    }

                    @Override
                    public void onSuccess(FormInstance result) {
                        if (result == null || result.getId() == null) {
                            formPanel.setValue(new FormInstance(classId, instanceId));
                        } else {
                            formPanel.setValue(result);
                        }
                    }
                });
            }
        });
    }
}
