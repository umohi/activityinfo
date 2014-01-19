package org.activityinfo.geo.migrate;

import com.google.appengine.tools.mapreduce.DatastoreMutationPool;
import com.google.appengine.tools.mapreduce.Mapper;
import com.vividsolutions.jts.io.WKBWriter;
import org.activityinfo.geo.rtree.*;
import org.activityinfo.server.database.hibernate.entity.AdminEntity;
import com.google.appengine.api.datastore.*;

import java.io.IOException;


public class StoreGeometryMapper extends Mapper<AdminEntity, Void, Void> {

    private transient RTreeIndex index;

    @Override
    public void beginSlice() {
        super.beginSlice();
        index = RTreeIndex.getOrCreate("admin2");
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
        index.insert(uri, value.getGeometry().getEnvelopeInternal());
    }
}
