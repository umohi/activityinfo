package org.activityinfo.geo.rtree;

import com.google.appengine.api.datastore.*;
import com.google.common.collect.Iterators;
import com.vividsolutions.jts.geom.Envelope;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;


public class RTreeIndex {

    public static final String TREE_KIND = "RTree";
    public static final String PAGE_KIND = "RTreeP";
    public static final String ROOT_KIND = "RTreeR";

    private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    private final RTreeConfig config;

    /**
     * The Datastore key of the RTree entity, which serves
     * as the root entity of our entity group.
     */
    private final Key parentKey;

    private final Key rootPointerKey;

    public static RTreeIndex getOrCreate(String name) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Transaction tx = datastore.beginTransaction();
        Key key = KeyFactory.createKey(TREE_KIND, name);
        try {
            Entity tree = datastore.get(tx, key);
            tx.commit();

            return new RTreeIndex(key, new RTreeConfig(50, 2));

        } catch (EntityNotFoundException notFoundException) {
            // create new tree
            try {
                Entity tree = new Entity(key);

                long rootId = datastore.allocateIds(PAGE_KIND, 1).iterator().next().getId();

                Entity rootPage = new Entity(PAGE_KIND, rootId, key);
                Page page = Page.emptyPage(rootId);
                rootPage.setProperty("b", new Blob(page.serialize()));

                Entity rootPointer = new Entity(rootPointerKey(key));
                rootPointer.setProperty("root", rootId);

                datastore.put(tx, Arrays.asList(tree, rootPage, rootPointer));
                tx.commit();

                return new RTreeIndex(key, new RTreeConfig(50, 2));

            } catch(Exception e) {
                tx.rollback();
                throw new RuntimeException(e);
            }
        }
    }

    private RTreeIndex(Key parentKey, RTreeConfig config) {
        this.parentKey = parentKey;
        this.rootPointerKey = rootPointerKey(parentKey);
        this.config = config;
    }

    private static Key rootPointerKey(Key parentKey) {
        return KeyFactory.createKey(parentKey, ROOT_KIND, "root");
    }

    public void insert(String dataUri, Envelope mbr) {
        ConsistentAccessor consistentAccessor = new ConsistentAccessor();
        try {
            Insertion insertion = new Insertion(config, consistentAccessor, new LinearSeedPicker());
            insertion.insert(new Bounded(mbr), dataUri);
            consistentAccessor.tx.commit();
        } catch(Exception e)  {
            consistentAccessor.tx.rollback();
            throw new RuntimeException("insertion failed", e);
        }
    }

    public boolean search(Envelope mbr, ResultVisitor visitor) {
        Accessor accessor = new Accessor();
        Search search = new Search(accessor);
        return search.search(new Bounded(mbr), visitor);
    }

    private Key pageKey(long id) {
        return KeyFactory.createKey(parentKey, PAGE_KIND, id);
    }

    private Page pageFromEntity(Entity entity) {
        Blob blob = (Blob) entity.getProperty("b");
        try {
            return Page.deserialize(entity.getKey().getId(), blob.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Accesses the pages outside of transactions.
     */
    private class Accessor implements TreeAccessor {

        @Override
        public Page loadPage(long pageId) {
            Entity entity = null;
            try {
                entity = datastore.get(pageKey(pageId));
            } catch (EntityNotFoundException e) {
                throw new IllegalStateException("Page " + pageId + " does not exist", e);
            }
            return pageFromEntity(entity);
        }

        @Override
        public long getRootPageId() {
            Entity rootPointer = null;
            try {
                rootPointer = datastore.get(rootPointerKey);
            } catch (EntityNotFoundException e) {
                throw new IllegalStateException("The tree has no root", e);
            }
            Long pageId = (Long) rootPointer.getProperty("root");
            return pageId;
        }
    }

    private class ConsistentAccessor implements MutatingTreeAccessor {

        private Transaction tx;
        private Iterator<Key> pageKeyIterator = Iterators.emptyIterator();

        private ConsistentAccessor() {
            this.tx = datastore.beginTransaction();
        }

        @Override
        public void persistPage(Page page) {
            Entity pageEntity = new Entity(pageKey(page.getId()));
            pageEntity.setProperty("b", new Blob(page.serialize()));
            datastore.put(tx, pageEntity);
        }

        @Override
        public long newPageId() {
            if(!pageKeyIterator.hasNext()) {
                pageKeyIterator = datastore.allocateIds(parentKey, PAGE_KIND, 10).iterator();
            }
            return pageKeyIterator.next().getId();
        }

        @Override
        public void setRootPage(long pageId) {
            Entity rootPointer = new Entity(rootPointerKey);
            rootPointer.setUnindexedProperty("root", pageId);
            datastore.put(tx, rootPointer);
        }

        @Override
        public Page loadPage(long pageId) {
            Entity entity = null;
            try {
                entity = datastore.get(tx, pageKey(pageId));
            } catch (EntityNotFoundException e) {
                throw new IllegalStateException("Page " + pageId + " does not exist", e);
            }
            return pageFromEntity(entity);
        }

        @Override
        public long getRootPageId() {
            Entity rootPointer = null;
            try {
                rootPointer = datastore.get(tx, rootPointerKey);
            } catch (EntityNotFoundException e) {
                throw new IllegalStateException("The tree has no root", e);
            }
            return (Long) rootPointer.getProperty("root");
        }
    }
}
