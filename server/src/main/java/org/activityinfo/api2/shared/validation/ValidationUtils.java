package org.activityinfo.api2.shared.validation;
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

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

import java.util.List;

/**
 * @author yuriyz on 3/11/14.
 */
public class ValidationUtils {

    private ValidationUtils() {
    }

    public static void show(List<ValidationFailure> failures, DivElement failuresContainer) {
        final SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
        for (ValidationFailure failure : failures) {
            safeHtmlBuilder.append(SafeHtmlUtils.fromString(failure.getMessage().getMessage().getValue()))
                    .appendHtmlConstant("<br/>");
        }
        failuresContainer.setInnerHTML(safeHtmlBuilder.toSafeHtml().asString());
    }

    public static String format(String controlName, String validationMessage) {
        return controlName + " - " + validationMessage;
    }
}
