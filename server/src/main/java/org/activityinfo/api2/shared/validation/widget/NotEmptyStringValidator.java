package org.activityinfo.api2.shared.validation.widget;
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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.user.client.ui.HasValue;
import org.activityinfo.api2.shared.LocalizedString;
import org.activityinfo.api2.shared.validation.ValidationFailure;
import org.activityinfo.api2.shared.validation.ValidationMessage;
import org.activityinfo.api2.shared.validation.ValidationUtils;
import org.activityinfo.api2.shared.validation.Validator;
import org.activityinfo.ui.full.client.i18n.I18N;

import java.util.Collections;
import java.util.List;

/**
 * @author yuriyz on 3/11/14.
 */
public class NotEmptyStringValidator implements Validator {

    private HasValue<String> control;
    private String controlName;

    public NotEmptyStringValidator(HasValue<String> control, String controlName) {
        this.control = control;
        this.controlName = controlName;
    }

    @Override
    public List<ValidationFailure> validate() {
        if (control != null && Strings.isNullOrEmpty(control.getValue())) {
            final ValidationFailure failure = new ValidationFailure();
            final String message = Strings.isNullOrEmpty(controlName) ? I18N.CONSTANTS.validationControlIsEmpty() :
                    ValidationUtils.format(controlName, I18N.CONSTANTS.validationControlIsEmpty());
            failure.setMessage(new ValidationMessage(new LocalizedString(message)));
            failure.setControl(control);
            return Lists.newArrayList(failure);
        }
        return Collections.emptyList();
    }
}
