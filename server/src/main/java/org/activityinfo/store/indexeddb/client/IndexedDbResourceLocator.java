package org.activityinfo.store.indexeddb.client;

import org.activityinfo.api2.client.InstanceQuery;
import org.activityinfo.api2.client.Promise;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Instance;
import org.activityinfo.api2.shared.Projection;
import org.activityinfo.api2.shared.criteria.Criteria;
import org.activityinfo.api2.shared.form.FormClass;
import org.activityinfo.api2.shared.form.FormInstance;

import java.util.List;

/**
 * Exposes an Indexeddb database as
 */
public class IndexedDbResourceLocator implements ResourceLocator {



    @Override
    public Promise<FormClass> getFormClass(Cuid formId) {
        return null;
    }

    @Override
    public Promise<FormInstance> getFormInstance(Cuid formId) {
        return null;
    }

    @Override
    public Promise<Void> persist(Instance instance) {
        return null;
    }

    @Override
    public Promise<Integer> countInstances(Criteria criteria) {
        return null;
    }

    @Override
    public Promise<List<FormInstance>> queryInstances(Criteria criteria) {
        return null;
    }

    @Override
    public Promise<List<Projection>> query(InstanceQuery query) {
        return null;
    }
}
