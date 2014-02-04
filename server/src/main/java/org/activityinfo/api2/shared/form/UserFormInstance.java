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

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.Resource;
import org.activityinfo.api2.shared.model.AiLatLng;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;

/**
 * @author yuriyz on 1/29/14.
 */
public class UserFormInstance implements Resource, FormInstance {

    private Iri id;
    private Iri definitionId;
    private final Map<Iri, Object> valueMap = Maps.newHashMap();

    public UserFormInstance(Iri id, Iri definitionId) {
        // id can be null : for example during new form creation
        Preconditions.checkNotNull(definitionId);
        this.id = id;
        this.definitionId = definitionId;
    }

    @Override
    public Iri getId() {
        return id;
    }

    public Iri getDefinitionId() {
        return definitionId;
    }

    public Map<Iri, Object> getValueMap() {
        return valueMap;
    }

    public void set(@NotNull Iri fieldId, Object fieldValue) {
        Preconditions.checkNotNull(fieldId);
        valueMap.put(fieldId, fieldValue);
    }

    public Object get(Iri fieldId) {
        return valueMap.get(fieldId);
    }

    public String getString(Iri fieldId) {
        final Object value = get(fieldId);
        if (value instanceof String) {
            return (String) value;
        }
        return null;
    }

    public Date getDate(Iri fieldId) {
        final Object value = get(fieldId);
        if (value instanceof Date) {
            return (Date) value;
        }
        return null;
    }

    public Double getDouble(Iri fieldId) {
        final Object value = get(fieldId);
        if (value instanceof Double) {
            return (Double) value;
        }
        return null;
    }

    public UserFormInstance copy() {
        final UserFormInstance copy = new UserFormInstance(getId(), getDefinitionId());
        copy.getValueMap().putAll(this.getValueMap());
        return copy;
    }

    public AiLatLng getAiLatLng(Iri fieldId) {
        final Object value = get(fieldId);
        if (value instanceof AiLatLng) {
            return (AiLatLng) value;
        }
        return null;
    }
}
