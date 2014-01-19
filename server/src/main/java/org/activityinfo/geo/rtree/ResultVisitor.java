package org.activityinfo.geo.rtree;


public interface ResultVisitor {

    boolean visit(String dataUri);
}
