package org.activityinfo.geo.rtree;

import java.util.Collection;
import java.util.LinkedList;


class Entry implements Node
{
    final Data entry;

    private Node parent;
    private float[] coords;
    private float[] dimensions;

    public Entry(float[] coords, float[] dimensions, Data entry)
    {
        this.coords = coords;
        this.dimensions = dimensions;
        this.entry = entry;
    }

    public String toString()
    {
        return "Entry: " + entry;
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public void setParent(Node parent) {
        this.parent = parent;
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
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLeaf() {
        // an entry isn't actually a leaf (its parent is a leaf)
        // but all the algorithms should stop at the first leaf they encounter,
        // so this little hack shouldn't be a problem.
        return true;
    }

    @Override
    public void addChild(Node n) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearChildren() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addChildren(Collection<Node> nodes) {
        throw new UnsupportedOperationException();
    }
}
