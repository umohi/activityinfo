package org.activityinfo.geo.migrate;

import com.google.appengine.tools.mapreduce.DatastoreMutationPool;
import com.google.appengine.tools.mapreduce.Mapper;
import com.vividsolutions.jts.io.WKBWriter;
import org.activityinfo.geo.rtree.*;
import org.activityinfo.server.database.hibernate.entity.AdminEntity;
import com.google.appengine.api.datastore.*;

import java.io.IOException;


public class StoreGeometryMapper extends Mapper<AdminEntity, Void, Void> {

    private transient DatastoreMutationPool mutationPool;
    private transient RTree index;
    private transient  DatastoreNodeStore store;

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
//        WKBWriter writer = new WKBWriter();
//        byte[] bytes = writer.write(value.getGeometry());
//
//        Entity entity = new Entity("Geom", uri);
//        entity.setProperty("wkb", new Blob(bytes));
//
//        mutationPool.put(entity);

        String uri = "adminEntity/" + value.getId();
        index.insert(value.getGeometry().getEnvelopeInternal(), new Data(uri));
        try {
            store.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void endSlice() {
        super.endSlice();
        mutationPool.flush();

    }
}
