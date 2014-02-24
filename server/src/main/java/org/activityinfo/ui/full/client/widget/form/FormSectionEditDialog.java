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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.api2.shared.form.FormSection;
import org.activityinfo.ui.full.client.style.TransitionUtil;
import org.activityinfo.ui.full.client.util.GwtUtil;

/**
 * @author yuriyz on 2/20/14.
 */
public class FormSectionEditDialog extends Composite {

    private static FormSectionEditDialogBinder uiBinder = GWT
            .create(FormSectionEditDialogBinder.class);

    interface FormSectionEditDialogBinder extends UiBinder<Widget, FormSectionEditDialog> {
    }

    private FormSection formSection;

    public FormSectionEditDialog() {
        TransitionUtil.ensureBootstrapInjected();
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setVisible(boolean visible) {
        GwtUtil.setVisible(getElement(), visible);
    }

    public FormSection getFormSection() {
        return formSection;
    }

    public void setFormSection(FormSection formSection) {
        this.formSection = formSection;
    }
}
