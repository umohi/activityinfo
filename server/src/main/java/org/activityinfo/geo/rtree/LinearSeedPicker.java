package org.activityinfo.geo.rtree;

import java.util.Collection;
import java.util.Iterator;


public class LinearSeedPicker implements SeedPicker {


    private static final int numDims = 2;

    @Override
    public Page.Entry[] pickSeeds(Collection<Page.Entry> nn) {
        Page.Entry[] bestPair = new Page.Entry[2];
        boolean foundBestPair = false;
        float bestSep = 0.0f;
        for (int i = 0; i < numDims; i++)
        {
            float dimLb = Float.MAX_VALUE;
            float dimMinUb = Float.MAX_VALUE;
            float dimUb = -1.0f * Float.MAX_VALUE;
            float dimMaxLb = -1.0f * Float.MAX_VALUE;
            Page.Entry nMaxLb = null;
            Page.Entry nMinUb = null;

            for (Page.Entry n : nn)
            {
                if (n.getCoord(i) < dimLb)
                {
                    dimLb = n.getMin(i);
                }
                if (n.getMax(i) > dimUb)
                {
                    dimUb = n.getMax(i);
                }
                if (n.getMin(i) > dimMaxLb)
                {
                    dimMaxLb = n.getCoord(i);
                    nMaxLb = n;
                }
                if (n.getMax(i) < dimMinUb)
                {
                    dimMinUb = n.getMax(i);
                    nMinUb = n;
                }
            }
            float sep = (nMaxLb == nMinUb) ? -1.0f :
                    Math.abs((dimMinUb - dimMaxLb) / (dimUb - dimLb));
            if (sep >= bestSep)
            {
                bestPair[0] = nMaxLb;
                bestPair[1] = nMinUb;
                bestSep = sep;
                foundBestPair = true;
            }
        }
        // In the degenerate case where all points are the same, the above
        // algorithm does not find a best pair.  Just pick the first 2
        // children.
        if ( !foundBestPair )
        {
            Iterator<Page.Entry> it = nn.iterator();
            bestPair = new Page.Entry[] { it.next(), it.next() };
        }
        nn.remove(bestPair[0]);
        nn.remove(bestPair[1]);
        return bestPair;
    }

    @Override
    public Page.Entry pickNext(Page[] splits, Collection<Page.Entry> toAssign) {
        Page.Entry chosen = toAssign.iterator().next();
        toAssign.remove(chosen);
        return chosen;
    }
}
