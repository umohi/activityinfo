package org.activityinfo.geo.rtree;

import com.google.appengine.api.datastore.*;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.*;
import java.util.*;


public class DatastoreNodeStore implements Store {

    public static final String TREE_KIND = "RTree";
    private static final String NODE_KIND = "RTreeN";

    private static final int ID_PREALLOC_SIZE = 10;

    private static final byte NODE_TYPE = 0;
    private static final byte ENTRY_TYPE = 1;

    private final DatastoreService datastore;

    private final Key treeKey;

    private Entity treeEntity;
    private DatastoreNode root = null;
    private boolean dirtyRoot = false;

    private Map<Long, DatastoreNode> loaded = Maps.newHashMap();


    private Iterator<Key> nodeKeys = Iterators.emptyIterator();

    public DatastoreNodeStore(DatastoreService datastore, Entity entity) {
        this.datastore = datastore;
        treeKey = entity.getKey();
        treeEntity = entity;
    }

    private long nextNodeId() {
        if(!nodeKeys.hasNext()) {
            nodeKeys =  datastore.allocateIds(treeKey, NODE_KIND, ID_PREALLOC_SIZE).iterator();
        }
        return nodeKeys.next().getId();
    }


    @Override
    public Node getRootNode() {
        if(root == null) {
            Entity tree = fetchTree();
            Long rootId = (Long)tree.getProperty("root");
            if(rootId == null) {
                return null;
            }
            root = new DatastoreNode();

            fetchNode(root, rootId);
            if(root.getCoords() == null || root.getDimensions() == null) {
                throw new IllegalStateException();
            }
        }
        return root;
    }

    private void fetchNode(DatastoreNode node, long id) {
        if(id == 0) {
            throw new IllegalArgumentException("Node id cannot be zero!");
        }
        try {
            Entity entity = datastore.get(KeyFactory.createKey(treeKey, NODE_KIND, id));
            Blob b = (Blob) entity.getProperty("b");
            deserialize(node, b.getBytes());

        } catch(Exception e) {
            throw new RuntimeException("Exception encountered while fetching node " + id, e);
        }
    }

    private void deserialize(DatastoreNode node, byte[] bytes) throws IOException {
        ByteArrayInputStream bos = new ByteArrayInputStream(bytes);
        DataInputStream in = new DataInputStream(bos);


        // write this nodes id
        node.parentId = in.readLong();

        // and the version...
        node.version = in.readLong();

        node.coords = readFloatArray(in);
        node.dimensions = readFloatArray(in);

        // read children in
        node.children = Lists.newLinkedList();
        node.leaf = true;
        int childCount = in.readInt();
        for(int i=0;i!=childCount;++i) {

            float [] coords = readFloatArray(in);
            float [] dimensions = readFloatArray(in);

            byte type = in.readByte();
            if(type == ENTRY_TYPE) {
                node.children.add(new Entry(coords, dimensions, Data.read(in)));
            } else {
                long childId = in.readLong();

                DatastoreNode childNode = loaded.get(childId);
                if(childNode == null) {
                    childNode = new DatastoreNode();
                    childNode.id = childId;
                }

                childNode.version = in.readLong();
                childNode.leaf = (in.readByte() == 1);
                childNode.coords = coords;
                childNode.dimensions = dimensions;
                loaded.put(childNode.id, childNode);
                node.children.add(childNode);
                node.leaf = false;
            }
        }
    }

    private Entity fetchTree() {
        if(treeEntity == null) {
            try {
                treeEntity = datastore.get(treeKey);
            } catch (EntityNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }
        return treeEntity;
    }

    @Override
    public void setRootNode(Node node) {
        root = (DatastoreNode) node;
        dirtyRoot = true;
    }

    @Override
    public Node createNode(float[] coords, float[] dimensions, boolean leaf) {
        DatastoreNode node = new DatastoreNode();
        node.id = nextNodeId();
        node.coords = coords;
        node.dimensions = dimensions;
        node.children = Lists.newLinkedList();
        node.leaf = leaf;
        node.dirty = true;

        loaded.put(node.id, node);

        return node;
    }



    /**
     * Flush all dirty nodes to the datastore
     */
    public void flush() throws IOException {
        List<Entity> toWrite = Lists.newArrayList();

        if(dirtyRoot) {
            Entity tree = fetchTree();
            tree.setProperty("root", root.id);
            toWrite.add(tree);
        }

        for(DatastoreNode node : loaded.values()) {
            if (node.dirty) {
                Entity entity = new Entity(NODE_KIND, node.id, treeKey);
                entity.setProperty("b", new Blob(node.serialize()));
                toWrite.add(entity);
            }
        }
        if(!toWrite.isEmpty()) {
            datastore.put(toWrite);
        }

        for(DatastoreNode node : loaded.values()) {
            node.dirty = false;
        }
    }

    private class DatastoreNode implements Node {
        private long id;
        private DatastoreNode parent;
        private float[] coords;
        private float[] dimensions;
        private LinkedList<Node> children = null;
        private long version;
        private boolean leaf;
        private boolean dirty;
        private long parentId = 0;


        @Override
        public Node getParent() {
            if(parentId != 0 && parent == null) {
                throw new UnsupportedOperationException();
            }
            return parent;
        }

        @Override
        public void setParent(Node parent) {
            this.parent = (DatastoreNode) parent;
            dirty = true;
        }

        @Override
        public float[] getCoords() {
            return coords;
        }

        @Override
        public float[] getDimensions() {
            return dimensions;
        }

        @Override
        public List<Node> getChildren() {
            if(children == null) {
                fetchNode(this, id);
            }
            return Collections.unmodifiableList(children);
        }

        @Override
        public boolean isLeaf() {
            return leaf;
        }

        @Override
        public void addChild(Node n) {
            children.add(n);
            dirty = true;
        }

        @Override
        public void clearChildren() {
            children.clear();
            dirty = true;
        }

        @Override
        public void addChildren(Collection<Node> nodes) {
            children.addAll(nodes);
            dirty = true;
        }

        private byte[] serialize() throws IOException {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);

            // write this node's id
            dos.writeLong(parent == null ? 0 : parent.id);

            // and the verison...
            dos.writeLong(version);

            // bounds
            writeFloatArray(dos, coords);
            writeFloatArray(dos, dimensions);

            // write children
            if(children == null) {
                dos.writeInt(0);
            } else {
                dos.writeInt(children.size());
                for(Node child : children) {
                    writeFloatArray(dos, child.getCoords());
                    writeFloatArray(dos, child.getDimensions());
                    if(child instanceof Entry) {
                        Entry entry = (Entry) child;
                        dos.writeByte(ENTRY_TYPE);
                        entry.entry.write(dos);

                    } else {
                        DatastoreNode childNode = (DatastoreNode) child;
                        dos.writeByte(NODE_TYPE);
                        dos.writeLong(childNode.id);
                        dos.writeLong(childNode.version);
                        dos.writeByte(childNode.leaf ? 1 : 0);
                    }
                }
            }
            dos.flush();
            return bos.toByteArray();
        }
    }

    private void writeFloatArray(DataOutputStream dos, float[] array) throws IOException {
        dos.writeFloat(array[0]);
        dos.writeFloat(array[1]);
    }

    private float[] readFloatArray(DataInputStream in) throws IOException {
        return new float[] { in.readFloat(), in.readFloat() };
    }
}
