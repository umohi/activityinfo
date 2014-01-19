package org.activityinfo.geo.rtree;


import java.util.LinkedList;
import java.util.List;

public class Search {

    private final TreeAccessor treeAccessor;

    public Search(TreeAccessor treeAccessor) {
        this.treeAccessor = treeAccessor;
    }

    public boolean search(Bounded rect, ResultVisitor visitor) {
        return search(rect, treeAccessor.loadPage(treeAccessor.getRootPageId()), visitor);
    }

    private boolean search(Bounded rect, Page page, ResultVisitor visitor) {
        for (Page.Entry e : page.getEntries()) {
            if(e.intersects(rect)) {
                boolean shouldContinue;
                if(page.isLeaf()) {
                    shouldContinue = visitor.visit(e.getDataUri());
                } else {
                    shouldContinue = search(rect, treeAccessor.loadPage(e.getPageId()), visitor);
                }
                if(!shouldContinue) {
                    return false;
                }
            }
        }
        return true;
    }

}
