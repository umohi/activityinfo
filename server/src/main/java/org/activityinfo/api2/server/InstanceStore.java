package org.activityinfo.api2.server;

import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Instance;
import org.activityinfo.api2.shared.RevisionId;
import org.activityinfo.api2.shared.form.FormInstance;

/**
 * Interface to a synchronous instance store
 */
public interface InstanceStore {


    /**
     * Persists an instance to the store.
     *
     * @param userId the author of the revision
     * @param parentRevisionId the revision on which this change was based.
     * @param instance the instance to be persisted
     */
    void persist(Cuid userId, RevisionId parentRevisionId, Instance instance);

    FormInstance fetchLatest(Cuid instanceId);

}
