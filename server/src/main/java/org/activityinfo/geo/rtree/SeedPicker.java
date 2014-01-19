package org.activityinfo.geo.rtree;


import java.util.Collection;

public interface SeedPicker {

    /**
     * Picks two (usually extreme) rectangles to use as the initial content of the
     * two empty split pages.
     *
     * The two chosen nodes are removed from the collection.
     *
     * @param toAssign pages to be assigned to the two splits
     * @return an array of two entries corresponding to the two pages. T
     */
    Page.Entry[] pickSeeds(Collection<Page.Entry> toAssign);

    /**
     * Pick the next entry to assign to
     * @param splits
     * @param toAssign
     * @return
     */
    Page.Entry pickNext(Page[] splits, Collection<Page.Entry> toAssign);
}
