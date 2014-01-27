package org.activityinfo.dev.client;
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

import com.google.gwt.user.client.Random;
import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.LocalizedString;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.FormFieldType;
import org.activityinfo.api2.shared.form.UserForm;

import java.util.Date;

/**
 * @author yuriyz on 1/24/14.
 */
public class DevUtils {
    private DevUtils() {
    }

    public static Iri randomIri() {
        return new Iri(Random.nextInt() + "_" + new Date().getTime());
    }

    static UserForm createTestUserForm() {
        final FormField e1 = new FormField(randomIri());
        e1.setType(FormFieldType.QUANTITY);
        e1.setDescription(new LocalizedString("Quantity description"));
        e1.setLabel(new LocalizedString("Quantity label"));


        final UserForm form = new UserForm(randomIri());
        form.addElement(e1);
        return form;
    }
}
