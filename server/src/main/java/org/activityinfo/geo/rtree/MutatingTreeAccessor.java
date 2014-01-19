package org.activityinfo.geo.rtree;


/**
 * Provides mutating access to an RTree
 */
public interface MutatingTreeAccessor extends TreeAccessor {

    void persistPage(Page page);

    long newPageId();

    void setRootPage(long pageId);

}
