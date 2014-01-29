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
import org.activityinfo.api2.shared.form.FormSection;
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
        final FormField item1 = new FormField(randomIri());
        item1.setType(FormFieldType.FREE_TEXT);
        item1.setDescription(new LocalizedString("The State Coordinator and different sections are involved in many meetings with the GRSS. When the specific objective of the meeting is to discuss PoC issues, these should be counted for the indicator. CAD, PoC Advisors and SOC should be responsible for providing information for this."));
        item1.setLabel(new LocalizedString("Advocacy meetings with GRSS to promote responsibility for PoC, tackle impunity and strenghten accountability"));
        item1.setUnit(new LocalizedString("Meetings"));

        final FormField item2 = new FormField(randomIri());
        item2.setType(FormFieldType.FREE_TEXT);
        item2.setDescription(new LocalizedString("Advocacy meetings with SPLA to improve compliance with human rights obigations in PoC high risk locations "));
        item2.setLabel(new LocalizedString("Advocacy meetings can be informal or formal and can seek to address internal conduct and discipline, but also focus on a proactive implementation of those obligations.  As above the objective of the meeting should be explicitly related to PoC. MLOs should provide this information."));
        item2.setUnit(new LocalizedString("Meetings"));

        final FormField quantityField = new FormField(randomIri());
        quantityField.setType(FormFieldType.QUANTITY);
        quantityField.setDescription(new LocalizedString("Quantity description"));
        quantityField.setLabel(new LocalizedString("Quantity label"));

        final FormField freeTextField = new FormField(randomIri());
        freeTextField.setType(FormFieldType.FREE_TEXT);
        freeTextField.setDescription(new LocalizedString("Free text description"));
        freeTextField.setLabel(new LocalizedString("Free text"));

        final FormField localDateField = new FormField(randomIri());
        localDateField.setType(FormFieldType.LOCAL_DATE);
        localDateField.setDescription(new LocalizedString("Local date description"));
        localDateField.setLabel(new LocalizedString("Local date"));

        final FormField geographicPointField = new FormField(randomIri());
        geographicPointField.setType(FormFieldType.GEOGRAPHIC_POINT);
        geographicPointField.setDescription(new LocalizedString("Geographic point description"));
        geographicPointField.setLabel(new LocalizedString("Geographic point"));

        final FormField referenceField = new FormField(randomIri());
        referenceField.setType(FormFieldType.REFERENCE);
        referenceField.setDescription(new LocalizedString("Reference description"));
        referenceField.setLabel(new LocalizedString("Reference point"));

        final FormSection section1 = new FormSection(randomIri());
        section1.setLabel(new LocalizedString("Tier 1 - Reduced physical threats to civilians"));
        section1.addElement(quantityField);
        section1.addElement(freeTextField);
        section1.addElement(localDateField);
        section1.addElement(geographicPointField);
        section1.addElement(referenceField);

        final FormSection section2 = new FormSection(randomIri());
        section2.setLabel(new LocalizedString("Tier 2 - GRSS fulfill PoC responsibility"));
        section2.addElement(item1);
        section2.addElement(item2);

        final UserForm form = new UserForm(randomIri());
        form.addElement(section1);
        form.addElement(section2);
        form.addElement(quantityField);
        form.addElement(freeTextField);
        return form;
    }
}
