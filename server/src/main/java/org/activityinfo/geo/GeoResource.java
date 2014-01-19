package org.activityinfo.geo;


import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.labs.repackaged.com.google.common.base.Joiner;
import com.google.appengine.tools.mapreduce.MapReduceJob;
import com.google.appengine.tools.mapreduce.MapReduceSettings;
import com.google.appengine.tools.mapreduce.MapReduceSpecification;
import com.google.appengine.tools.mapreduce.Marshallers;
import com.google.appengine.tools.mapreduce.outputs.NoOutput;
import com.google.appengine.tools.mapreduce.reducers.NoReducer;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import org.activityinfo.geo.migrate.AdminEntityInput;
import org.activityinfo.geo.migrate.StoreGeometryMapper;
import org.activityinfo.geo.rtree.RTree;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

public class GeoResource {

    @Path("/migrate")
    public Response migrateGeometry() throws URISyntaxException {
        MapReduceSettings settings = new MapReduceSettings().setWorkerQueueName("mapreduce-workers")
                .setBucketName("activityinfo")
                .setModule(null);
        String jobId = MapReduceJob.start(
                MapReduceSpecification.of(
                        "Create MapReduce entities",
                        new AdminEntityInput(),
                        new StoreGeometryMapper(),
                        Marshallers.getVoidMarshaller(),
                        Marshallers.getVoidMarshaller(),
                        NoReducer.<Void, Void, Void>create(),
                        NoOutput.<Void, Void>create(1)),
                settings);

        return Response.seeOther(new URI("/_ah/pipeline/status.html?root=" + jobId)).build();
    }

//    @GET
//    @Path("/rtree/{name}")
//    @Produces("text/html")
//    public String getRtree(@PathParam("name") String name) throws EntityNotFoundException {
//        DatastoreNodeStore store = DatastoreRTreeFactory.get(name);
//        RTree tree = new RTree(store);
//        return tree.visualize();
//    }
//
//
//    @GET
//    @Path("/query")
//    @Produces("text/plain")
//    public String query(@QueryParam("x") double x, @QueryParam("y") double y) throws EntityNotFoundException {
//
////        DatastoreNodeStore store = DatastoreRTreeFactory.get("admin");
////        RTree tree = new RTree(store);
////
////        return Joiner.on("\n").join(tree.search(new Envelope(new Coordinate(x, y))));
//        return ""
//    }
}
