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
import org.activityinfo.legacy.shared.reports.util.mapping.Extents;

/**
 * One-to-one DTO for the
 * {@link org.activityinfo.server.database.hibernate.entity.AdminEntity} domain
 * object.
 *
 * @author Alex Bertram
 */
public final class AdminEntityDTO extends BaseModelData implements DTO {
    private Extents bounds;

    public AdminEntityDTO() {
    }

    /**
     * @param levelId the id of the AdminLevel to which this AdminEntity belongs
     * @param id      the id of this AdminEntity
     * @param name    the name of this AdminEntity
     */
    public AdminEntityDTO(int levelId, int id, String name) {
        setId(id);
        setName(name);
        setLevelId(levelId);
    }

    /**
     * @param levelId  the id of the AdminLevel to which this AdminEntity belongs
     * @param id       the id of this AdminEntity
     * @param parentId the id of this AdminEntity's parent
     * @param name     this AdminEntity's name
     */
    public AdminEntityDTO(int levelId, int id, int parentId, String name) {
        setId(id);
        setParentId(parentId);
        setName(name);
        setLevelId(levelId);
    }

    /**
     * @param levelId the id of the AdminLevel to which this AdminEntity belongs
     * @param id      the id of this AdminEntity
     * @param name    the name of this AdminEntity
     * @param bounds  the geographing BoundingBox of this AdminEntity
     */
    public AdminEntityDTO(int levelId, int id, String name, Extents bounds) {
        setId(id);
        setName(name);
        setLevelId(levelId);
        setBounds(bounds);
    }

    /**
     * @param levelId  the id of the AdminLevel to which this AdminEntity belongs
     * @param id       the id of this AdminEntity
     * @param parentId the id of this AdminEntity's parent
     * @param name     the name of this AdminEntity
     * @param bounds   the geographing BoundingBox of this AdminEntity
     */
    public AdminEntityDTO(int levelId, int id, int parentId, String name,
                          Extents bounds) {
        setId(id);
        setLevelId(levelId);
        setParentId(parentId);
        setName(name);
        setBounds(bounds);
    }

    /**
     * Sets this AdminEntity's id
     */
    public void setId(int id) {
        set("id", id);
    }

    /**
     * @return this AdminEntity's id
     */
    public int getId() {
        return (Integer) get("id");
    }

    /**
     * @return this AdminEntity's name
     */
    public String getName() {
        return get("name");
    }

    /**
     * Sets this AdminEntity's name
     */
    public void setName(String name) {
        set("name", name);
    }

    /**
     * @return the id of this AdminEntity's corresponding
     * {@link org.activityinfo.server.database.hibernate.entity.AdminLevel}
     */
    public int getLevelId() {
        return (Integer) get("levelId");
    }

    /**
     * Sets the id of the AdminLevel to which this AdminEntity belongs
     */
    public void setLevelId(int levelId) {
        set("levelId", levelId);
    }

    /**
     * Sets the id of this AdminEntity's parent.
     */
    public void setParentId(Integer value) {
        set("parentId", value);
    }

    /**
     * @return the id of this AdminEntity's corresponding parent AdminEntity
     */
    public Integer getParentId() {
        return get("parentId");
    }

    /**
     * @return true if this AdminEntity has non-null bounds
     */
    public boolean hasBounds() {
        return getBounds() != null;
    }

    /**
     * @return the geographic Extents of this AdminEntity
     */
    public Extents getBounds() {
        return bounds;
    }

    /**
     * Sets the Extents of this AdminEntity.
     */
    public void setBounds(Extents bounds) {
        this.bounds = bounds;
    }

    /**
     * Gets the property name for a given AdminLevel when AdminEntities are
     * stored in pivoted form.
     *
     * @param levelId the id of the AdminLevel
     * @return the property name
     */
    public static String getPropertyName(int levelId) {
        return AdminLevelDTO.getPropertyName(levelId);
    }

    /**
     * @return the property name used for this AdminEntity's AdminLevel when
     * stored in pivoted form
     */
    public String getPropertyName() {
        return AdminLevelDTO.getPropertyName(this.getLevelId());
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        return get("id").hashCode();
    }

    /**
     * Tests for equality based on ID.
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (!(other instanceof AdminEntityDTO)) {
            return false;
        }

        AdminEntityDTO that = (AdminEntityDTO) other;
        return getId() == that.getId();
    }
}
