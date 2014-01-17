package org.activityinfo.geo.rtree;

public interface Store {
    Node createNode(float[] coords, float[] dimensions, boolean leaf);

    Node getRootNode();

    void setRootNode(Node node);
}
