package org.activityinfo.ui.full.client.page.entry.place;
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
import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.form.UserFormType;
import org.activityinfo.ui.full.client.page.PageStateParser;

/**
 * @author yuriyz on 2/3/14.
 */
public class UserFormPlaceParser implements PageStateParser {

    private static final String FORM_PARAMETER_NAME = "form";
    private static final String FORM_INSTANCE_PARAMETER_NAME = "instance";
    private static final String TYPE_PARAMETER_NAME = "type";

    public static String serialize(UserFormPlace place) {
        final StringBuilder fragment = new StringBuilder();
        if (place.getUserFormId() != null) {
            fragment.append(FORM_PARAMETER_NAME).append("=").append(place.getUserFormId().asString());
            fragment.append("+");
        }
        if (place.getUserFormInstanceId() != null) {
            fragment.append(FORM_INSTANCE_PARAMETER_NAME).append("=").append(place.getUserFormInstanceId().asString());
            fragment.append("+");
        }
        // type is not null only for new form -> therefore used only for new form creation
        if (place.getUserFormType() != null) {
            fragment.append(TYPE_PARAMETER_NAME).append("=").append(place.getUserFormType().getTokenValue());
        }
        return fragment.toString();
    }

    public static UserFormPlace parseToken(String token) {
        final UserFormPlace place = new UserFormPlace();
        if (!Strings.isNullOrEmpty(token)) {
            final String searchString = UserFormPlace.PAGE_ID.getId() + "/";
            final int indexOf = token.indexOf(searchString);
            if (indexOf != -1) {
                token = token.substring(indexOf + searchString.length());
            }

            final String[] parts = token.split("\\+");
            for (String part : parts) {
                final String[] subParts = part.split("\\=");
                if (subParts.length == 2) {
                    final String key = subParts[0];
                    final String value = subParts[1];
                    if (!Strings.isNullOrEmpty(value)) {
                        if (FORM_PARAMETER_NAME.equals(key)) {
                            place.setUserFormId(new Iri(value));
                        }
                        if (FORM_INSTANCE_PARAMETER_NAME.equals(key)) {
                            place.setUserFormInstanceId(new Iri(value));
                        }
                        if (TYPE_PARAMETER_NAME.equals(key)) {
                            place.setUserFormType(UserFormType.valueOf(value));
                        }
                    }
                }
            }
        }
        return place;
    }

    @Override
    public UserFormPlace parse(String token) {
        return parseToken(token);
    }
}
