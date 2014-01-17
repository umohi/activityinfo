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
import org.activityinfo.geo.rtree.DatastoreNodeStore;
import org.activityinfo.geo.rtree.DatastoreRTreeFactory;
import org.activityinfo.geo.rtree.RTree;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

public class GeoResource {

    @Path("/migrate")
    public Response migrateGeometry() throws URISyntaxException {
        MapReduceSettings settings = new MapReduceSettings().setWorkerQueueName("mapreduce-workers")
                .setBucketName("activityinfo").setModule("mapreduce");
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

    @Path("/query")
    @Produces("text/plain")
    public String query(@QueryParam("x") double x, @QueryParam("y") double y) throws EntityNotFoundException {

        DatastoreNodeStore store = DatastoreRTreeFactory.get("admin");
        RTree tree = new RTree(store);

        return Joiner.on("\n").join(tree.search(new Envelope(new Coordinate(x, y))));
    }
}
