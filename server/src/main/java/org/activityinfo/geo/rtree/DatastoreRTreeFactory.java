package org.activityinfo.geo.rtree;

import com.google.appengine.api.datastore.*;

import java.util.Arrays;

public class DatastoreRTreeFactory {

    public static DatastoreNodeStore create(String name, int maxEntries) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Transaction tx = datastore.beginTransaction();
        Entity tree = createTree(datastore, tx, name, maxEntries);
        tx.commit();

        return new DatastoreNodeStore(datastore, tree);
    }


    public static DatastoreNodeStore get(String name) throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity tree = datastore.get(KeyFactory.createKey(DatastoreNodeStore.TREE_KIND, name));
        return new DatastoreNodeStore(datastore, tree);

    }


    public static DatastoreNodeStore getOrCreate(String name, int maxEntries) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Transaction tx = datastore.beginTransaction();
        Entity tree;
        try {
            tree = datastore.get(tx, KeyFactory.createKey(DatastoreNodeStore.TREE_KIND, name));
        } catch (EntityNotFoundException e) {
            tree = createTree(datastore, tx, name, maxEntries);
        }
        tx.commit();
        return new DatastoreNodeStore(datastore, tree);
    }


    private static Entity createTree(DatastoreService datastore, Transaction tx, String name, int maxEntries) {
        Key treeKey = KeyFactory.createKey(DatastoreNodeStore.TREE_KIND, name);

     //   Entity rootNode = new Entity(datastore.allocateIds(treeKey, NODE_KIND, 1).getStart());

        Entity tree = new Entity(DatastoreNodeStore.TREE_KIND, name);
//        tree.setUnindexedProperty(MAX_ENTRIES, maxEntries);
//        tree.setUnindexedProperty(MIN_ENTRIES, 2);
//        tree.setUnindexedProperty(ROOT_NODE_ID, rootNode.getKey());

        datastore.put(tx, Arrays.asList(tree));

        return tree;
    }

}
