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

import org.activityinfo.api.shared.adapter.CuidAdapter;
import org.activityinfo.api2.client.Promise;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.Projection;
import org.activityinfo.api2.shared.Resource;
import org.activityinfo.api2.shared.criteria.ClassCriteria;
import org.activityinfo.api2.shared.criteria.Criteria;
import org.activityinfo.api2.shared.form.FormClass;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.form.tree.FieldPath;

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
    public Promise<Integer> countInstances(Criteria criteria) {
        return null;
    }

    @Override
    public Promise<List<FormInstance>> queryInstances(Criteria criteria) {
        if (criteria instanceof ClassCriteria) {
            final Iri iri = ((ClassCriteria) criteria).getClassIri();
            final int legacyId = CuidAdapter.getLegacyIdFromCuidIri(iri);
            return Promise.resolved(DevUtils.getFormInstanceList(legacyId));
        }

        return Promise.rejected(new UnsupportedOperationException());
    }

    @Override
    public Promise<List<Projection>> query(List<FieldPath> paths, Criteria criteria) {
        return Promise.rejected(new UnsupportedOperationException());
    }
}
