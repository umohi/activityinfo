package org.activityinfo.store.appengine;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import org.activityinfo.api2.server.InstanceStore;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Instance;
import org.activityinfo.api2.shared.form.FormInstance;

/**
 * Instance Store built on top of the AppEngine data store.
 */
public class AppEngineInstanceStore implements InstanceStore {

    private final DatastoreService datastoreService;

    public AppEngineInstanceStore() {
        this.datastoreService = DatastoreServiceFactory.getDatastoreService();
    }

    @Override
    public void persist(Cuid userId, Instance instance) {

        // first persist the

    }

    @Override
    public FormInstance fetchLatest(Cuid instanceId) {
        return null;
    }
}
