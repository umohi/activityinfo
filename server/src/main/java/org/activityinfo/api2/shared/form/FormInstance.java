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

import com.bedatadriven.rebar.time.calendar.LocalDate;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.storage.onestore.v3.OnestoreEntity;
import org.activityinfo.api.shared.model.DTO;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Instance;
import org.activityinfo.api2.shared.form.has.HasHashCode;
import org.activityinfo.api2.shared.hash.HashCode;
import org.activityinfo.api2.shared.model.AiLatLng;
import org.activityinfo.api2.shared.types.FieldValue;
import org.activityinfo.api2.shared.types.RealValue;
import org.activityinfo.api2.shared.types.ReferenceValue;
import org.activityinfo.api2.shared.types.TextValue;
import org.activityinfo.api2.shared.types.temporal.LocalDateValue;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author yuriyz on 1/29/14.
 */
public class FormInstance implements Instance, HasHashCode {

    private Cuid id;
    private Cuid classId;
    private final Map<Cuid, FieldValue> valueMap = Maps.newHashMap();
    private Cuid parentId;
    private HashCode hashCode;

    /**
     * Constructs a new FormInstance. To obtain an id for a new instance
     * use
     *
     * @param id the id of the instance.
     * @param classId the id of this form's class
     */
    public FormInstance(@Nonnull Cuid id, @Nonnull Cuid classId) {
        Preconditions.checkNotNull(id, classId);
        this.id = id;
        this.classId = classId;
    }

    @Override
    public Cuid getId() {
        return id;
    }

    public Cuid getClassId() {
        return classId;
    }

    public void setParentId(Cuid parentId) {
        this.parentId = parentId;
    }

    public Cuid getParentId() {
        return parentId;
    }

    public Map<Cuid, FieldValue> getValueMap() {
        return valueMap;
    }

    public void removeAll(Set<Cuid> fieldIds) {
        for (Cuid fieldId : fieldIds) {
            valueMap.remove(fieldId);
        }
    }

    public void set(@NotNull Cuid fieldId, FieldValue value) {
        Preconditions.checkNotNull(fieldId);
        valueMap.put(fieldId, value);
    }

    public void set(@NotNull Cuid fieldId, Object fieldValue) {
        Preconditions.checkNotNull(fieldId);
        if (fieldValue instanceof LocalDate) {
            set(fieldId, LocalDateValue.valueOf((LocalDate) fieldValue));
        }

        if (fieldValue instanceof Date) {
            set(fieldId, LocalDateValue.valueOf((Date)fieldValue));
        }

        if (fieldValue instanceof DTO) {
            throw new IllegalArgumentException("Please use cuid reference instead of legacy class.");
        }

        if (fieldValue instanceof String) {
            set(fieldId, TextValue.valueOf((String)fieldValue));
        }

        if (fieldValue instanceof Number) {
            set(fieldId, RealValue.valueOf((Number)fieldValue));
        }

        if (fieldValue instanceof Cuid) {
            set(fieldId, new ReferenceValue((Cuid)fieldValue));
        }
    }

    public Object get(Cuid fieldId) {
        return valueMap.get(fieldId);
    }

    public String getString(Cuid fieldId) {
        final Object value = get(fieldId);
        if (value instanceof String) {
            return (String) value;
        }
        return null;
    }

    public LocalDate getDate(Cuid fieldId) {
        final Object value = get(fieldId);
        if (value instanceof Date) {
            return (LocalDate) value;
        }
        return null;
    }

    public Cuid getInstanceId(Cuid fieldId) {
        final Object value = get(fieldId);
        if(value instanceof Cuid) {
            return (Cuid) value;
        }
        return null;
    }


    public Set<Cuid> getReferences(Cuid fieldId) {
        final Object value = get(fieldId);
        if(value instanceof Cuid) {
            return Collections.singleton((Cuid)value);
        } else if(value instanceof Set) {
            return (Set<Cuid>)value;
        }
        return null;
    }

    public Double getDouble(Cuid fieldId) {
        final Object value = get(fieldId);
        if (value instanceof Double) {
            return (Double) value;
        }
        return null;
    }

    public FormInstance copy() {
        final FormInstance copy = new FormInstance(getId(), getClassId());
        copy.getValueMap().putAll(this.getValueMap());
        return copy;
    }

    public AiLatLng getPoint(Cuid fieldId) {
        final Object value = get(fieldId);
        if (value instanceof AiLatLng) {
            return (AiLatLng) value;
        }
        return null;
    }

    public HashCode getHashCode() {
        return hashCode;
    }

    public void setHashCode(HashCode hashCode) {
        this.hashCode = hashCode;
    }


    @Override
    public String toString() {
        return "FormInstance{" +
                "id=" + id +
                ", classId=" + classId +
                ", valueMap=" + valueMap +
                '}';
    }

}
