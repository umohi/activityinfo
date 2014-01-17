package org.activityinfo.geo.rtree;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.repackaged.com.google.common.io.Resources;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.base.Charsets;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InMemoryRTreeTest  {


    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void test() throws IOException {


        RTree tree = new RTree(new InMemStore());

        load(tree);
        query(tree);

    }


    @Test
    public void testAppEngine() throws IOException, EntityNotFoundException {

        DatastoreNodeStore store = DatastoreRTreeFactory.getOrCreate("test", 50);
        RTree tree = new RTree(store);

        load(tree);
        store.flush();

        // now query from the datastore
        store = DatastoreRTreeFactory.get("test");
        tree = new RTree(store);

        query(tree);
    }

    private void query(RTree tree) {
        //
//        141947: Territoire:Nyiragongo
//        141801: Province:Nord Kivu
//        141833: District:Nord Kivu
//        141946: Territoire:Masisi
//        141941: Territoire:Communes de Goma
//        141832: District:Ville de Goma
//        141965: Territoire:Idjwi
//        141800: Province:Orientale
//        141803: Province:Sud Kivu
//        141839: District:Sud Kivu

        Coordinate goma = new Coordinate(29.212646, -1.647722);


        for(Data result : tree.search(new Envelope(goma))) {
            System.out.println(result);
        }
    }

    private void load(RTree tree) throws IOException {
        List<String> lines = Resources.readLines(getClass().getResource("rdc.csv"), Charsets.UTF_8);
        for(String line : lines) {
            String[] columns = line.split("\t");
            long id = Integer.parseInt(columns[0]);
            String level = columns[1];
            String name = columns[2];
            double x1 = Double.parseDouble(columns[3]);
            double y1 = Double.parseDouble(columns[4]);
            double x2 = Double.parseDouble(columns[5]);
            double y2 = Double.parseDouble(columns[6]);
            Envelope mbr = new Envelope(x1, x2, y1, y2);

            tree.insert(mbr, new Data(level + ": " + name));
        }
    }

}
