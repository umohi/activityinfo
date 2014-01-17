package org.activityinfo.geo.rtree;


import java.util.Collection;
import java.util.LinkedList;

public class InMemStore implements Store {

    private InMemNode rootNode;

    @Override
    public Node createNode(float[] coords, float[] dimensions, boolean leaf) {
        return new InMemNode(coords, dimensions, leaf);
    }

    @Override
    public Node getRootNode() {
        return rootNode;
    }

    @Override
    public void setRootNode(Node node) {
        rootNode = (InMemNode) node;
    }

    static class InMemNode implements Node {
        private final float[] coords;
        private final float[] dimensions;
        private final LinkedList<Node> children;
        private final boolean leaf;

        private InMemNode parent;

        InMemNode(float[] coords, float[] dimensions, boolean leaf)
        {
            this.coords = new float[coords.length];
            this.dimensions = new float[dimensions.length];
            System.arraycopy(coords, 0, this.getCoords(), 0, coords.length);
            System.arraycopy(dimensions, 0, this.getDimensions(), 0, dimensions.length);
            this.leaf = leaf;
            children = new LinkedList<Node>();
        }


        @Override
        public InMemNode getParent() {
            return parent;
        }

        @Override
        public void setParent(Node parent) {
            this.parent = (InMemNode)parent;
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
        public LinkedList<Node> getChildren() {
            return children;
        }

        @Override
        public boolean isLeaf() {
            return leaf;
        }

        @Override
        public void addChild(Node n) {
            children.add(n);
        }

        @Override
        public void clearChildren() {
            children.clear();
        }

        @Override
        public void addChildren(Collection<Node> nodes) {
            children.addAll(nodes);
        }
    }
}
