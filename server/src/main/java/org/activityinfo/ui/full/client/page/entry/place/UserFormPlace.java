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

import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.form.UserFormType;
import org.activityinfo.ui.full.client.page.PageId;
import org.activityinfo.ui.full.client.page.PageState;
import org.activityinfo.ui.full.client.page.app.Section;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

/**
 * @author yuriyz on 1/31/14.
 */
public class UserFormPlace implements PageState {

    public static final PageId PAGE_ID = new PageId("site-form");

    private UserFormType userFormType;
    private Iri userFormId;
    private Iri userFormInstanceId;

    public UserFormPlace() {
    }

    /**
     * Used when new form should be created.
     *
     * @param userFormType user form type to create.
     */
    public UserFormPlace(@Nonnull UserFormType userFormType) {
        this.userFormType = userFormType;
    }

    public UserFormPlace(Iri userFormId, Iri userFormInstanceId) {
        this.userFormId = userFormId;
        this.userFormInstanceId = userFormInstanceId;
    }

    public UserFormType getUserFormType() {
        return userFormType;
    }

    @Override
    public PageId getPageId() {
        return PAGE_ID;
    }

    @Override
    public String serializeAsHistoryToken() {
        return UserFormPlaceParser.serialize(this);
    }

    @Override
    public List<PageId> getEnclosingFrames() {
        return Arrays.asList(PAGE_ID);
    }

    @Override
    public Section getSection() {
        return Section.DATA_ENTRY;
    }

    public void setUserFormType(UserFormType userFormType) {
        this.userFormType = userFormType;
    }

    public Iri getUserFormId() {
        return userFormId;
    }

    public void setUserFormId(Iri userFormId) {
        this.userFormId = userFormId;
    }

    public Iri getUserFormInstanceId() {
        return userFormInstanceId;
    }

    public void setUserFormInstanceId(Iri userFormInstanceId) {
        this.userFormInstanceId = userFormInstanceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserFormPlace that = (UserFormPlace) o;

        if (userFormId != null ? !userFormId.equals(that.userFormId) : that.userFormId != null) return false;
        if (userFormInstanceId != null ? !userFormInstanceId.equals(that.userFormInstanceId) : that.userFormInstanceId != null)
            return false;
        if (userFormType != that.userFormType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userFormType != null ? userFormType.hashCode() : 0;
        result = 31 * result + (userFormId != null ? userFormId.hashCode() : 0);
        result = 31 * result + (userFormInstanceId != null ? userFormInstanceId.hashCode() : 0);
        return result;
    }
}
