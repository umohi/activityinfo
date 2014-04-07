package org.activityinfo.ui.client.widget;
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
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.ui.client.style.ElementStyle;
import org.activityinfo.ui.client.util.GwtUtil;

/**
 * @author yuriyz on 4/1/14.
 */
public class ConfirmDialog<T> extends ModalDialog {

    // action performed on "ok" (primary button click)
    public static interface ConfirmAction<T> {
        void perform(ConfirmDialogCallback<T> callback);
    }

    public static enum State {
        CONFIRM, PROGRESS, FAILED
    }

    public interface WarningTemplate extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("<p class='text-warning'>{0}</p>")
        SafeHtml html(String message);
    }

    public interface OkButtonTemplate extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("<img src='ActivityInfo/gxt231/images/default/grid/loading.gif'/>{0}")
        SafeHtml html(String progressState);
    }

    private static final WarningTemplate WARNING_TEMPLATE = GWT.create(WarningTemplate.class);
    private static final OkButtonTemplate OK_BTN_TEMPLATE = GWT.create(OkButtonTemplate.class);

    public static final State INITIAL_STATE = State.CONFIRM;

    private final ConfirmDialogCallback<T> callback;
    private final ConfirmDialogResources resources;
    private final HTML failedMessageContainer = new HTML();
    private final HTML messageContainer = new HTML();

    private State state = ConfirmDialog.INITIAL_STATE;

    public ConfirmDialog(ConfirmDialogResources resources, ElementStyle primaryButtonStyle,
                         final ConfirmDialog.ConfirmAction<T> confirmAction) {
        this.resources = resources;

        getOkButton().setStyleName("btn btn-" + primaryButtonStyle.name().toLowerCase());
        getModalBody().add(failedMessageContainer);
        getModalBody().add(messageContainer);
        setState(State.CONFIRM);

        callback = new ConfirmDialogCallback<>(this);

        // handlers
        getOkButtonHandler().removeHandler();
        getOkButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setState(State.PROGRESS);
                confirmAction.perform(callback);
            }
        });
    }

    public void setState(State state) {
        this.state = state;
        switch (state) {
            case CONFIRM:
                getOkButton().setEnabled(true);
                setDialogTitle(resources.getConfirmTitle());
                messageContainer.setHTML(SafeHtmlUtils.fromString(resources.getConfirmMessage()));
                getOkButton().setText(resources.getConfirmOkButtonText());
                GwtUtil.setVisible(false, failedMessageContainer.getElement());
                break;
            case FAILED:
                failedMessageContainer.setHTML(WARNING_TEMPLATE.html(I18N.CONSTANTS.unexpectedException()));
                messageContainer.setHTML(SafeHtmlUtils.fromString(resources.getFailedMessage()));
                getOkButton().setEnabled(true);
                getOkButton().setText(resources.getFailedOkButtonText());
                GwtUtil.setVisible(true, failedMessageContainer.getElement());
                break;
            case PROGRESS:
                setDialogTitle(resources.getProgressTitle());
                messageContainer.setHTML(SafeHtmlUtils.fromString(resources.getProgressMessage()));
                getOkButton().setEnabled(false);
                getOkButton().setHTML(OK_BTN_TEMPLATE.html(resources.getProgressOkButtonText()));
                GwtUtil.setVisible(false, failedMessageContainer.getElement());
                break;
        }
    }

    public State getState() {
        return state;
    }
}
