package org.activityinfo.geo.rtree;


/**
 * Provides access to the pages of an RTree
 */
public interface TreeAccessor {

    Page loadPage(long pageId);

    long getRootPageId();
}
