package org.activityinfo.geo.rtree;

import com.vividsolutions.jts.geom.Envelope;

import java.util.LinkedList;
import java.util.Stack;

/**
 *
 */
public class Insertion {
    private final RTreeConfig config;
    private final MutatingTreeAccessor storage;
    private final SeedPicker seedPicker;

    /**
     * Maintains a list of all of the parents through which we've descended
     * to reach the insertion point. We have to climb back up and update them
     * when we're finished.
     */
    private Stack<Page> parents = new Stack<>();

    public Insertion(RTreeConfig config, MutatingTreeAccessor storage, SeedPicker seedPicker) {
        this.config = config;
        this.storage = storage;
        this.seedPicker = seedPicker;
    }

    public void insert(Envelope envelope, String dataUri) {
        insert(new Bounded(envelope), dataUri);
    }

    /**
     * Inserts the given entry into the RTree, associated with the given
     * rectangle.
     *
     */
    public void insert(Bounded rect, String dataUri)
    {
        Page.Entry newEntry = Page.newDataEntry(dataUri, rect);
        
        Page leaf = chooseLeaf(storage.loadPage(storage.getRootPageId()), newEntry);

        // has this resource already been added?
        if(!leaf.containsData(dataUri)) {
            leaf.addEntry(newEntry);

            updateTree(leaf);
        }
    }

    /**
     * Choose the best leaf node in which to insert the new rectangle.
     */
    private Page chooseLeaf(Page rootPage, Page.Entry e) {
        Page page = rootPage;
        while (!page.isLeaf()) {

            // keep track of this parent - we'll need to update later
            parents.add(rootPage);

            // among the entries in this page, find the one with the rectangle
            // that would need to expand the *least* to accommodate the new rectangle
            Page.Entry child = findEntryRequiringLeastExpansion(rootPage, e);
            page = storage.loadPage(child.getPageId());
        }
        return page;
    }


    /**
     * Split an over-full page into two distinct pages with the least overflow
     */
    private Page[] splitPage(Page page) {

        LinkedList<Page.Entry> toAssign = new LinkedList<>(page.getEntries());

        Page[] splits = new Page[]
                { Page.emptyPage(page.getId()),
                  Page.emptyPage(storage.newPageId()) };

        // Pick the seeds will use to initialize our two split nodes.
        Page.Entry[] seeds = seedPicker.pickSeeds(toAssign);
        splits[0].addEntry(seeds[0]);
        splits[1].addEntry(seeds[1]);

        // Now assign the remainder of the entries
        while (!toAssign.isEmpty())
        {
            // make sure one of the splits doesn't get all the nodes. if we go below
            // a certain minimum, then assign all remaining to the less filled page
            if ((splits[0].getEntries().size() >= config.getMinEntries())
                    && (splits[1].getEntries().size() + toAssign.size() == config.getMinEntries()))
            {
                splits[1].addEntries(toAssign);

                return splits;
            }
            else if ((splits[1].getEntries().size() >= config.getMinEntries())
                    && (splits[0].getEntries().size() + toAssign.size() == config.getMinEntries()))
            {
                splits[0].addEntries(toAssign);
                return splits;
            }

            // if neither page is too empty, then continue with
            // our seedPicker algorithm
            Page.Entry nextToAssign = seedPicker.pickNext(splits, toAssign);
            Page preferred = chooseBestPage(splits, nextToAssign);
            preferred.addEntry(nextToAssign);
        }
        return splits;
    }

    /**
     * Choose the best page to assign the entry {@code c}
     * @param splits the two split nodes
     * @param entry the entry to assign
     * @return the best page
     */
    private Page chooseBestPage(Page[] splits, Page.Entry entry) {
        Page preferred;
        float e0 = splits[0].requiredExpansion(entry);
        float e1 = splits[1].requiredExpansion(entry);
        if (e0 < e1) {
            preferred = splits[0];

        } else if (e0 > e1) {
            preferred = splits[1];

        } else {
            float a0 = splits[0].area();
            float a1 = splits[1].area();
            if (a0 < a1)
            {
                preferred = splits[0];
            }
            else if (e0 > a1)
            {
                preferred = splits[1];
            }
            else
            {
                if (splits[0].getEntries().size() < splits[1].getEntries().size()) {
                    preferred = splits[0];

                } else if (splits[0].getEntries().size() > splits[1].getEntries().size()) {
                    preferred = splits[1];

                } else {
                    preferred = splits[(int) Math.round(Math.random())];
                }
            }
        }
        return preferred;
    }

    private void updateTree(Page leaf) {

        // first split the leaf if it's exceeded capacity
        // this can have a riple effect all the way up the tree

        Page page = leaf;
        while (page.getEntryCount() > config.getMaxEntries()) {
            Page[] splits = splitPage(page);
            storage.persistPage(splits[0]);
            storage.persistPage(splits[1]);

            if(parents.isEmpty()) {
                Page newRoot = Page.emptyPage(storage.newPageId());
                newRoot.addEntry(Page.newEntry(splits[0]));
                newRoot.addEntry(Page.newEntry(splits[1]));
                storage.persistPage(newRoot);
                storage.setRootPage(newRoot.getId());
                return;

            } else {
                Page parent = parents.pop();
                parent.expandTo(splits[0]);
                parent.addEntry(splits[1]);
                storage.persistPage(parent);

                page = parent;
            }
        }
        storage.persistPage(page);

        // for the remaining parents, update their bounds if necessary
        while(!parents.isEmpty()) {
            Page parent = parents.pop();
            if(parent.contains(leaf)) {
                break;
            } else {
                parent.expandTo(leaf);
                storage.persistPage(parent);
            }
        }
    }


    private Page.Entry findEntryRequiringLeastExpansion(Page page, Page.Entry newEntry) {
        float minIncrease = Float.MAX_VALUE;
        Page.Entry best = null;
        for (Page.Entry entry : page.getEntries()) {

            float increasedArea = entry.requiredExpansion(newEntry);

            if (increasedArea < minIncrease) {
                minIncrease = increasedArea;
                best = entry;

            } else if (increasedArea == minIncrease) {
                // if there is a tie, choose the smallest entry in absolute terms
                if(entry.area() < best.area()) {
                    best = entry;
                }
            }
        }
        return best;
    }
}