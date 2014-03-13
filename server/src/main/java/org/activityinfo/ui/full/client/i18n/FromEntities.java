package org.activityinfo.ui.full.client.i18n;

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

import org.activityinfo.core.shared.form.FormFieldType;
import org.activityinfo.legacy.shared.command.DimensionType;
import org.activityinfo.ui.full.client.Log;

import static org.activityinfo.i18n.shared.I18N.CONSTANTS;

public class FromEntities {

    public static final FromEntities INSTANCE = new FromEntities();

    public String getDimensionTypePluralName(DimensionType dimension) {
        switch (dimension) {
            case Activity:
                return removeSpaces(CONSTANTS.activities());
            case AdminLevel:
                return removeSpaces(CONSTANTS.adminEntities());
            case Partner:
                return removeSpaces(CONSTANTS.partners().trim());
            case Project:
                return removeSpaces(CONSTANTS.projects());
            case AttributeGroup:
                return removeSpaces(CONSTANTS.attributeTypes());
            case Indicator:
                return removeSpaces(CONSTANTS.indicators());
            case Target:
                return removeSpaces(CONSTANTS.targets());
            case Site:
                return removeSpaces(CONSTANTS.sites());
            case Database:
                return removeSpaces(CONSTANTS.databases());
            case Location:
                return removeSpaces(CONSTANTS.locations());
        }
        return "No pluralized string definition for " + dimension.toString();
    }

    public String getFormFieldType(FormFieldType type) {
        switch (type) {
            case REFERENCE:
                return CONSTANTS.fieldTypeReference();
            case QUANTITY:
                return CONSTANTS.fieldTypeQuantity();
            case LOCAL_DATE:
                return CONSTANTS.fieldTypeDate();
            case FREE_TEXT:
                return CONSTANTS.fieldTypeText();
            case GEOGRAPHIC_POINT:
                return CONSTANTS.fieldTypeGeographicPoint();
            case NARRATIVE:
                return CONSTANTS.fieldTypeBigText();
        }
        Log.warn("No translation for " + type);
        return type.name().toLowerCase();
    }

    private static String removeSpaces(String stringWithSpaces) {
        return stringWithSpaces.replaceAll(" ", "").toLowerCase();
    }
}
