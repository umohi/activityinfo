package org.activityinfo.ui.full.client.component.form.dialog;
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

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.ui.full.client.i18n.I18N;
import org.activityinfo.ui.full.client.widget.ModalDialog;
import org.activityinfo.ui.full.client.component.form.FormFieldTypeCombobox;

/**
 * @author yuriyz on 3/4/14.
 */
public class ChangeFormFieldTypeDialog extends ModalDialog {

    private final FormField formField;
    private final FormFieldTypeCombobox type = new FormFieldTypeCombobox();

    public ChangeFormFieldTypeDialog(FormField formField) {
        this.formField = formField;

        setDialogTitle(I18N.CONSTANTS.changeType());
        setOkButtonState();
        type.setTypes(formField.getType().getAllowedConvertTo());
        getModalBody().appendChild(type.getElement());
        getModalBody().appendChild(new HTML(SafeHtmlUtils.fromString(I18N.CONSTANTS.changeTypeWarning())).getElement());
    }

    public FormFieldTypeCombobox getType() {
        return type;
    }

    private void setOkButtonState() {
        getOkButton().setEnabled(type.hasTypes());
    }
}
