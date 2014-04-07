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

import com.google.common.base.Preconditions;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.ui.client.style.ElementStyle;

import javax.annotation.Nonnull;

/**
 * @author yuriyz on 4/1/14.
 */
public class ConfirmDialog extends ModalDialog {

    public static interface Listener {
        void onYes();

        void onNo();
    }

    public static class ListenerAdapter implements Listener {
        @Override
        public void onYes() {
        }

        @Override
        public void onNo() {
        }
    }

    public ConfirmDialog(@Nonnull String message, @Nonnull Listener listener) {
        this(I18N.CONSTANTS.confirmation(), message, ElementStyle.PRIMARY, listener);
    }

    public ConfirmDialog(@Nonnull String title, @Nonnull String message, @Nonnull ElementStyle primaryButtonStyle, @Nonnull final Listener listener) {
        Preconditions.checkNotNull(title);
        Preconditions.checkNotNull(message);
        Preconditions.checkNotNull(listener);

        setDialogTitle(title);
        getModalBody().add(new HTML(SafeHtmlUtils.fromString(message)));
        getOkButton().setStyleName("btn btn-" + primaryButtonStyle.name().toLowerCase());
        getOkButton().setText(I18N.CONSTANTS.yes());

        // handlers
        getOkButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                listener.onYes();
            }
        });
        getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                listener.onNo();
            }
        });
    }
}
