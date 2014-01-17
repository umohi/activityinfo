package org.activityinfo.geo.rtree;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by alex on 1/17/14.
 */
public interface Node {
    Node getParent();

    void setParent(Node parent);

    float[] getCoords();

    float[] getDimensions();

    List<Node> getChildren();

    boolean isLeaf();

    void addChild(Node n);

    void clearChildren();

    void addChildren(Collection<Node> nodes);
}
