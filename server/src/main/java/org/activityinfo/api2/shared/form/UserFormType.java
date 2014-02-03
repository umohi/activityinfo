package org.activityinfo.api2.shared.form;
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

/**
 * @author yuriyz on 2/3/14.
 */
public enum UserFormType {
    ACTIVITY("activity", UserFormInstanceType.SITE),
    LOCATION_TYPE("location", UserFormInstanceType.LOCATION),
    ATTRIBUTE_GROUP("attribute", UserFormInstanceType.ATTRIBUTE),
    ADMIN_LEVEL("admin_level", UserFormInstanceType.ADMIN_ENTITY);

    private final String tokenValue;
    private final UserFormInstanceType instanceType;

    UserFormType(String tokenValue, UserFormInstanceType instanceType) {
        this.tokenValue = tokenValue;
        this.instanceType = instanceType;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public UserFormInstanceType getInstanceType() {
        return instanceType;
    }

    public UserFormType fromTokenValue(String tokenValue) {
        if (!Strings.isNullOrEmpty(tokenValue)) {
            for (UserFormType type : values()) {
                if (type.getTokenValue().equals(tokenValue)) {
                    return type;
                }
            }
        }
        return null;
    }
}
