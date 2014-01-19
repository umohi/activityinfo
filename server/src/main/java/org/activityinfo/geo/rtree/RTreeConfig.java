package org.activityinfo.geo.rtree;

/**
 * Defines the parameters of an RTree. Cannot
 * be changed once created.
 */
public class RTreeConfig {

    private final int maxEntries;
    private final int minEntries;

    public RTreeConfig(int maxEntries, int minEntries) {
        this.maxEntries = maxEntries;
        this.minEntries = minEntries;
    }

    public int getMaxEntries() {
        return maxEntries;
    }

    public int getMinEntries() {
        return minEntries;
    }
}
