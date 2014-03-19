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

import org.activityinfo.core.client.InstanceQuery;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.Iri;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.Resource;
import org.activityinfo.core.shared.criteria.ClassCriteria;
import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;

import java.util.List;

/**
 * @author yuriyz on 2/14/14.
 */
public class DevResourceLocatorAdaptor implements ResourceLocator {

    @Override
    public Promise<FormClass> getFormClass(Cuid formId) {
        return null;
    }

    @Override
    public Promise<FormInstance> getFormInstance(Cuid formId) {
        return null;
    }

    @Override
    public Promise<Void> persist(Resource resource) {
        return null;
    }

    @Override
    public Promise<Void> persist(List<? extends Resource> resources) {
        return null;
    }

    @Override
    public Promise<Integer> countInstances(Criteria criteria) {
        return null;
    }

    @Override
    public Promise<List<FormInstance>> queryInstances(Criteria criteria) {
        if (criteria instanceof ClassCriteria) {
            final Iri iri = ((ClassCriteria) criteria).getClassIri();
            if (FormClass.CLASS_ID.asIri().equals(iri)) {
                return Promise.resolved(DevUtils.getFormInstanceList(DevUtils.MULTIPLE_SMALL_ID));
            }
            final int legacyId = CuidAdapter.getLegacyIdFromCuidIri(iri);
            return Promise.resolved(DevUtils.getFormInstanceList(legacyId));
        }

        return Promise.rejected(new UnsupportedOperationException());
    }

    @Override
    public Promise<List<Projection>> query(InstanceQuery query) {
        return Promise.rejected(new UnsupportedOperationException());
    }
}
