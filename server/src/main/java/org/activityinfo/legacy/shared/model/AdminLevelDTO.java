package org.activityinfo.legacy.shared.model;

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

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * One-to-one DTO for the
 * {@link org.activityinfo.server.database.hibernate.entity.AdminLevel} domain
 * object.
 *
 * @author Alex Bertram
 */
public final class AdminLevelDTO extends BaseModelData implements DTO {
    public static final String PROPERTY_PREFIX = "E";

    public AdminLevelDTO() {
    }

    /**
     * @param id   this AdminLevel's id
     * @param name this AdminLevel's name
     */
    public AdminLevelDTO(int id, String name) {
        super();
        setId(id);
        setName(name);
    }

    public AdminLevelDTO(int id, int parentId, String name) {
        super();
        setId(id);
        setParentLevelId(parentId);
        setName(name);
    }

    public AdminLevelDTO(int id, AdminLevelDTO parent, String name) {
        super();
        setId(id);
        setParentLevelId(parent.getId());
        setName(name);
    }

    /**
     * Sets the id of this AdminLevel
     */
    public void setId(int id) {
        set("id", id);
    }

    /**
     * @return the id of this AdminLevel
     */
    public int getId() {
        return (Integer) get("id");
    }

    /**
     * Sets the name of this AdminLevel
     */
    public void setName(String name) {
        set("name", name);
    }

    /**
     * @return the name of this AdminLevel
     */
    public String getName() {
        return get("name");
    }

    /**
     * @return the id of this AdminLevel's parent AdminLevel
     */
    public Integer getParentLevelId() {
        return get("parentLevelId");
    }

    /**
     * Sets the id of this AdminLevel's parent AdminLevel
     */
    public void setParentLevelId(Integer value) {
        set("parentLevelId", value);
    }

    /**
     * @return true if this AdminLevel is s root AdminLevel within it's Country
     */
    public boolean isRoot() {
        return get("parentLevelId") == null;
    }

    /**
     * Gets the propertyName for the given AdminLevel when stored in pivoted
     * form.
     *
     * @param levelId
     * @return The name of the property for this AdminLevel when stored in
     * pivoted form
     */
    public static String getPropertyName(int levelId) {
        return PROPERTY_PREFIX + levelId;
    }

    /**
     * @return the propertyName to be used for this AdminLevel when stored in
     * pivoted form
     */
    public String getPropertyName() {
        return getPropertyName(this.getId());
    }

    /**
     * Parses an admin propertyName for the referenced AdminLevel
     *
     * @param propertyName
     * @return
     */
    public static int levelIdForPropertyName(String propertyName) {
        return Integer.parseInt(propertyName
                .substring(AdminLevelDTO.PROPERTY_PREFIX.length()));
    }

    public void setCountryId(int countryId) {
        set("countryId", countryId);
    }

    public int getCountryId() {
        return (Integer) get("countryId");
    }

    public boolean getPolygons() {
        return get("polygons", false);
    }

    public void setPolygons(boolean polygons) {
        set("polygons", polygons);
    }

    @Override
    public String toString() {
        return getName();
    }

}
