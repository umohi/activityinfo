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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.shared.Log;
import org.activityinfo.ui.client.component.form.event.PersistEvent;
import org.activityinfo.ui.client.style.legacy.icon.ImageResources;
import org.activityinfo.ui.client.widget.Button;
import org.activityinfo.ui.client.widget.ButtonWithSize;
import org.activityinfo.ui.client.widget.ModalDialog;

/**
 * @author yuriyz on 3/28/14.
 */
public class FormPanelDialog extends ModalDialog {

    private static final String SCROLL_HEIGHT = "500px";

    private final ResourceLocator resourceLocator;
    private final FormPanel formPanel;
    private final ScrollPanel scrollPanel = new ScrollPanel();
    private final Image progressImage = new Image(ImageResources.ICONS.progress());

    public FormPanelDialog(ResourceLocator resourceLocator) {
        super();
        this.resourceLocator = resourceLocator;
        formPanel = new FormPanel(resourceLocator);
        formPanel.getEventBus().addHandler(PersistEvent.TYPE, new PersistEvent.Handler() {
            @Override
            public void persist(PersistEvent p_event) {
                onPersist();
            }
        });
        configureFooter();
    }

    private void configureFooter() {
        // hide form panel buttons and show them in dialog footer instead
        formPanel.setSaveButtonVisible(false);
        formPanel.setResetButtonVisible(false);

        final Button saveButton = new Button(ButtonWithSize.ButtonStyle.PRIMARY);
        saveButton.setText(I18N.CONSTANTS.save());
        saveButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                formPanel.onSave(event);
            }
        });
        final Button resetButton = new Button(ButtonWithSize.ButtonStyle.DEFAULT);
        resetButton.setText(I18N.CONSTANTS.reset());
        resetButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                formPanel.onReset(event);
            }
        });

        getModalFooter().remove(getOkButton());
        getModalFooter().remove(getCancelButton());

        getModalFooter().add(saveButton);
        getModalFooter().add(resetButton);
        getModalFooter().add(getCancelButton());
    }

    public void onPersist() {

    }

    public FormPanel getFormPanel() {
        return formPanel;
    }

    @Override
    public void show() {
        throw new UnsupportedOperationException("Please use show(final Cuid classId, final Cuid instanceId) instead.");
    }

    public void show(final Cuid classId, final Cuid instanceId) {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                scrollPanel.setHeight(SCROLL_HEIGHT);
                getModalBody().add(scrollPanel);
                scrollPanel.add(progressImage);
                getDialogDiv().setAttribute("style", "width:auto;"); // override modal-dialog width
                getOkButton().addStyleName("hidden");
                getDialog().center();
                formPanel.setDesignEnabled(false);

                load(classId, instanceId);
            }
        });
    }

    private void load(final Cuid classId, final Cuid instanceId) {
        resourceLocator.getFormClass(classId).then(new AsyncCallback<FormClass>() {
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Unable to fetch UserForm, iri=" + classId, caught);
                scrollPanel.remove(progressImage);
            }

            @Override
            public void onSuccess(FormClass result) {
                scrollPanel.remove(progressImage);
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
