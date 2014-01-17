package org.activityinfo.geo.migrate;

import com.google.appengine.tools.mapreduce.DatastoreMutationPool;
import com.google.appengine.tools.mapreduce.Mapper;
import com.vividsolutions.jts.io.WKBWriter;
import org.activityinfo.geo.rtree.*;
import org.activityinfo.server.database.hibernate.entity.AdminEntity;
import com.google.appengine.api.datastore.*;

import java.io.IOException;


public class StoreGeometryMapper extends Mapper<AdminEntity, Void, Void> {

    private DatastoreMutationPool mutationPool;
    private RTree index;
    private DatastoreNodeStore store;

    @Override
    public void beginSlice() {
        super.beginSlice();
        mutationPool = DatastoreMutationPool.forManualFlushing();
        store = DatastoreRTreeFactory.getOrCreate("admin", 50);
        index = new RTree(store);
    }

    @Override
    public void map(AdminEntity value) {

        // serialize to the data store
        WKBWriter writer = new WKBWriter();
        byte[] bytes = writer.write(value.getGeometry());

        String uri = "adminEntity/" + value.getId();
        Entity entity = new Entity("Geom", uri);
        entity.setProperty("wkb", new Blob(bytes));

        mutationPool.put(entity);
        index.insert(value.getGeometry().getEnvelopeInternal(), new Data(uri));
    }

    @Override
    public void endSlice() {
        super.endSlice();
        mutationPool.flush();
        try {
            store.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
