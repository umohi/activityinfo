package org.activityinfo.api2.client;


import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Instance;
import org.activityinfo.api2.shared.Projection;
import org.activityinfo.api2.shared.criteria.Criteria;
import org.activityinfo.api2.shared.form.FormClass;
import org.activityinfo.api2.shared.form.FormInstance;

import java.util.List;

public interface ResourceLocator {

    /**
     * Fetches the user form.
     *
     * @param formId
     * @return
     */
    Promise<FormClass> getFormClass(Cuid formId);

    Promise<FormInstance> getFormInstance(Cuid formId);

    /**
     * Persists a resource to the server, creating or updating as necessary.
     *
     * @param instance the resource to persist.
     * @return a Promise that resolves when the persistance operation completes
     * successfully.
     */
    Promise<Void> persist(Instance instance);


    /**
     * Counts the form instances that match the given criteria.
     * @param criteria
     */
    Promise<Integer> countInstances(Criteria criteria);

    /**
     * Retrieves the form instances that match the given criteria.
     * @param criteria
     */
    Promise<List<FormInstance>> queryInstances(Criteria criteria);

    Promise<List<Projection>> query(InstanceQuery query);

}
