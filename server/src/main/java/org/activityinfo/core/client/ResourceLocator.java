package org.activityinfo.core.client;


import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.Resource;
import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.fp.client.Promise;

import java.util.List;

public interface ResourceLocator {

    /**
     * Fetches the user form.
     *
     * @param formId
     * @return
     */
    Promise<FormClass> getFormClass(Cuid formId);

    /**
     * todo yuriy->alex: please check it. Is it correct way to list all possible classes for range?
     * Fetches all form classes.
     *
     * @return all form classes
     */
    Promise<List<FormClass>> getFormClass();

    Promise<FormInstance> getFormInstance(Cuid formId);

    /**
     * Persists a resource to the server, creating or updating as necessary.
     *
     * @param resource the resource to persist.
     * @return a Promise that resolves when the persistance operation completes
     * successfully.
     */
    Promise<Void> persist(Resource resource);


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
