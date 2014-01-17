package org.activityinfo.geo.rtree;


import com.vividsolutions.jts.geom.Envelope;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * Implementation of an arbitrary-dimension RTree. Based on R-Trees: A Dynamic
 * Index Structure for Spatial Searching (Antonn Guttmann, 1984)
 *
 * This class is not thread-safe.
 *
 * Copyright 2010 Russ Weeks rweeks@newbrightidea.com Licensed under the GNU
 * LGPL License details here: http://www.gnu.org/licenses/lgpl-3.0.txt
 *

 */
public class RTree
{
    public enum SeedPicker { LINEAR, QUADRATIC }

    private final int maxEntries;
    private final int minEntries;
    private final int numDims;

    private final float[] pointDims;

    private final SeedPicker seedPicker;

    private Store store;

    private volatile int size;

    /**
     * Creates a new RTree.
     *
     * @param maxEntries
     *          maximum number of entries per node
     * @param minEntries
     *          minimum number of entries per node (except for the root node)
     * @param numDims
     *          the number of dimensions of the RTree.
     */
    public RTree(Store store, int maxEntries, int minEntries, int numDims, SeedPicker seedPicker)
    {
        assert (minEntries <= (maxEntries / 2));
        this.store = store;
        this.numDims = numDims;
        this.maxEntries = maxEntries;
        this.minEntries = minEntries;
        this.seedPicker = seedPicker;
        pointDims = new float[numDims];
        if(store.getRootNode() == null) {
            store.setRootNode(buildRoot(true));
        }
    }


    private Node buildRoot(boolean asLeaf)
    {
        float[] initCoords = new float[numDims];
        float[] initDimensions = new float[numDims];
        for (int i = 0; i < this.numDims; i++)
        {
            initCoords[i] = (float) Math.sqrt(Float.MAX_VALUE);
            initDimensions[i] = -2.0f * (float) Math.sqrt(Float.MAX_VALUE);
        }
        return store.createNode(initCoords, initDimensions, asLeaf);
    }

    /**
     * Builds a new RTree using default parameters: maximum 50 entries per node
     * minimum 2 entries per node 2 dimensions
     */
    public RTree(Store store)
    {
        this(store, 50, 2, 2, SeedPicker.LINEAR);
    }

    /**
     * @return the maximum number of entries per node
     */
    public int getMaxEntries()
    {
        return maxEntries;
    }

    /**
     * @return the minimum number of entries per node for all nodes except the
     *         root.
     */
    public int getMinEntries()
    {
        return minEntries;
    }

    /**
     * @return the number of dimensions of the tree
     */
    public int getNumDims()
    {
        return numDims;
    }

    /**
     * @return the number of items in this tree.
     */
    public int size()
    {
        return size;
    }

    public List<Data> search(Envelope mbr) {
        return search(coords(mbr), dimensions(mbr));
    }

    /**
     * Searches the RTree for objects overlapping with the given rectangle.
     *
     * @param coords
     *          the corner of the rectangle that is the lower bound of every
     *          dimension (eg. the top-left corner)
     * @param dimensions
     *          the dimensions of the rectangle.
     * @return a list of objects whose rectangles overlap with the given
     *         rectangle.
     */
    public List<Data> search(float[] coords, float[] dimensions)
    {
        assert (coords.length == numDims);
        assert (dimensions.length == numDims);
        LinkedList<Data> results = new LinkedList<Data>();
        search(coords, dimensions, store.getRootNode(), results);
        return results;
    }

    private void search(float[] coords, float[] dimensions, Node n,
                        LinkedList<Data> results)
    {
        if (n.isLeaf())
        {
            for (Node e : n.getChildren())
            {
                if (isOverlap(coords, dimensions, e.getCoords(), e.getDimensions()))
                {
                    results.add(((Entry) e).entry);
                }
            }
        }
        else
        {
            for (Node c : n.getChildren())
            {
                if (isOverlap(coords, dimensions, c.getCoords(), c.getDimensions()))
                {
                    search(coords, dimensions, c, results);
                }
            }
        }
    }

    /**
     * Deletes the entry associated with the given rectangle from the RTree
     *
     * @param coords
     *          the corner of the rectangle that is the lower bound in every
     *          dimension
     * @param dimensions
     *          the dimensions of the rectangle
     * @param entry
     *          the entry to delete
     * @return true iff the entry was deleted from the RTree.
     */
    public boolean delete(float[] coords, float[] dimensions, Data entry)
    {
        assert (coords.length == numDims);
        assert (dimensions.length == numDims);
        Node l = findLeaf(store.getRootNode(), coords, dimensions, entry);
        if ( l == null ) {
            System.out.println("WTF?");
            findLeaf(store.getRootNode(), coords, dimensions, entry);
        }
        assert (l != null) : "Could not find leaf for entry to delete";
        assert (l.isLeaf()) : "Entry is not found at leaf?!?";
        ListIterator<Node> li = l.getChildren().listIterator();
        Data removed = null;
        while (li.hasNext())
        {
            @SuppressWarnings("unchecked")
            Entry e = (Entry) li.next();
            if (e.entry.equals(entry))
            {
                removed = e.entry;
                li.remove();
                break;
            }
        }
        if (removed != null)
        {
            condenseTree(l);
            size--;
        }
        if ( size == 0 )
        {
            store.setRootNode(buildRoot(true));
        }
        return (removed != null);
    }

    public boolean delete(float[] coords, Data entry)
    {
        return delete(coords, pointDims, entry);
    }

    private Node findLeaf(Node n, float[] coords, float[] dimensions, Data entry)
    {
        if (n.isLeaf())
        {
            for (Node c : n.getChildren())
            {
                if (((Entry) c).entry.equals(entry))
                {
                    return n;
                }
            }
            return null;
        }
        else
        {
            for (Node c : n.getChildren())
            {
                if (isOverlap(c.getCoords(), c.getDimensions(), coords, dimensions))
                {
                    Node result = findLeaf(c, coords, dimensions, entry);
                    if (result != null)
                    {
                        return result;
                    }
                }
            }
            return null;
        }
    }

    private void condenseTree(Node n)
    {
        Set<Node> q = new HashSet<Node>();
        while (n != store.getRootNode())
        {
            if (n.isLeaf() && (n.getChildren().size() < minEntries))
            {
                q.addAll(n.getChildren());
                n.getParent().getChildren().remove(n);
            }
            else if (!n.isLeaf() && (n.getChildren().size() < minEntries))
            {
                // probably a more efficient way to do this...
                LinkedList<Node> toVisit = new LinkedList<Node>(n.getChildren());
                while (!toVisit.isEmpty())
                {
                    Node c = toVisit.pop();
                    if (c.isLeaf())
                    {
                        q.addAll(c.getChildren());
                    }
                    else
                    {
                        toVisit.addAll(c.getChildren());
                    }
                }
                n.getParent().getChildren().remove(n);
            }
            else
            {
                tighten(n);
            }
            n = n.getParent();
        }
        if ( store.getRootNode().getChildren().size() == 0 )
        {
            store.setRootNode(buildRoot(true));
        }
        else if ( (store.getRootNode().getChildren().size() == 1) && (!store.getRootNode().isLeaf()) )
        {
            store.setRootNode(store.getRootNode().getChildren().get(0));
            store.getRootNode().setParent(null);
        }
        else
        {
            tighten(store.getRootNode());
        }
        for (Node ne : q)
        {
            @SuppressWarnings("unchecked")
            Entry e = (Entry) ne;
            insert(e.getCoords(), e.getDimensions(), e.entry);
        }
        size -= q.size();
    }

    /**
     * Empties the RTree
     */
    public void clear()
    {
        store.setRootNode(buildRoot(true));
        // let the GC take care of the rest.
    }

    public void insert(Envelope envelope, Data entry) {
        insert(coords(envelope), dimensions(envelope), entry);
    }

    private float[] dimensions(Envelope envelope) {
        return new float[] { (float)envelope.getWidth(), (float)envelope.getHeight() };
    }

    private float[] coords(Envelope envelope) {
        return new float[] { (float)envelope.getMinX(), (float)envelope.getMinY() };
    }

    /**
     * Inserts the given entry into the RTree, associated with the given
     * rectangle.
     *
     * @param coords
     *          the corner of the rectangle that is the lower bound in every
     *          dimension
     * @param dimensions
     *          the dimensions of the rectangle
     * @param entry
     *          the entry to insert
     */
    public void insert(float[] coords, float[] dimensions, Data entry)
    {
        assert (coords.length == numDims);
        assert (dimensions.length == numDims);
        Entry e = new Entry(coords, dimensions, entry);
        Node l = chooseLeaf(store.getRootNode(), e);
        l.addChild(e);
        size++;
        e.setParent(l);
        if (l.getChildren().size() > maxEntries)
        {
            Node[] splits = splitNode(l);
            adjustTree(splits[0], splits[1]);
        }
        else
        {
            adjustTree(l, null);
        }
    }

    /**
     * Convenience method for inserting a point
     * @param coords
     * @param entry
     */
    public void insert(float[] coords, Data entry)
    {
        insert(coords, pointDims, entry);
    }

    private void adjustTree(Node n, Node nn)
    {
        if (n == store.getRootNode())
        {
            if (nn != null)
            {
                // build new root and add children.
                Node root = buildRoot(false);
                store.setRootNode(root);
                root.addChild(n);
                n.setParent(root);
                root.addChild(nn);
                nn.setParent(root);
            }
            tighten(store.getRootNode());
            return;
        }
        tighten(n);
        if (nn != null)
        {
            tighten(nn);
            if (n.getParent().getChildren().size() > maxEntries)
            {
                Node[] splits = splitNode(n.getParent());
                adjustTree(splits[0], splits[1]);
            }
        }
        if (n.getParent() != null)
        {
            adjustTree(n.getParent(), null);
        }
    }

    private Node[] splitNode(Node n)
    {
        // TODO: this class probably calls "tighten" a little too often.
        // For instance the call at the end of the "while (!cc.isEmpty())" loop
        // could be modified and inlined because it's only adjusting for the addition
        // of a single node.  Left as-is for now for readability.
        @SuppressWarnings("unchecked")
        Node[] nn = new Node[]
                { n, store.createNode(n.getCoords(), n.getDimensions(), n.isLeaf())};
        nn[1].setParent(n.getParent());
        if (nn[1].getParent() != null)
        {
            nn[1].getParent().addChild(nn[1]);
        }
        LinkedList<Node> cc = new LinkedList<Node>(n.getChildren());
        n.clearChildren();
        Node[] ss = seedPicker == SeedPicker.LINEAR ? lPickSeeds(cc) : qPickSeeds(cc);
        nn[0].addChild(ss[0]);
        nn[1].addChild(ss[1]);
        tighten(nn);
        while (!cc.isEmpty())
        {
            if ((nn[0].getChildren().size() >= minEntries)
                    && (nn[1].getChildren().size() + cc.size() == minEntries))
            {
                nn[1].addChildren(cc);
                cc.clear();
                tighten(nn); // Not sure this is required.
                return nn;
            }
            else if ((nn[1].getChildren().size() >= minEntries)
                    && (nn[0].getChildren().size() + cc.size() == minEntries))
            {
                nn[0].addChildren(cc);
                cc.clear();
                tighten(nn); // Not sure this is required.
                return nn;
            }
            Node c = seedPicker == SeedPicker.LINEAR ? lPickNext(cc) : qPickNext(cc, nn);
            Node preferred;
            float e0 = getRequiredExpansion(nn[0].getCoords(), nn[0].getDimensions(), c);
            float e1 = getRequiredExpansion(nn[1].getCoords(), nn[1].getDimensions(), c);
            if (e0 < e1)
            {
                preferred = nn[0];
            }
            else if (e0 > e1)
            {
                preferred = nn[1];
            }
            else
            {
                float a0 = getArea(nn[0].getDimensions());
                float a1 = getArea(nn[1].getDimensions());
                if (a0 < a1)
                {
                    preferred = nn[0];
                }
                else if (e0 > a1)
                {
                    preferred = nn[1];
                }
                else
                {
                    if (nn[0].getChildren().size() < nn[1].getChildren().size())
                    {
                        preferred = nn[0];
                    }
                    else if (nn[0].getChildren().size() > nn[1].getChildren().size())
                    {
                        preferred = nn[1];
                    }
                    else
                    {
                        preferred = nn[(int) Math.round(Math.random())];
                    }
                }
            }
            preferred.addChild(c);
            tighten(preferred);
        }
        return nn;
    }

    // Implementation of Quadratic PickSeeds
    private Node[] qPickSeeds(LinkedList<Node> nn)
    {
        @SuppressWarnings("unchecked")
        Node[] bestPair = new Node[2];
        float maxWaste = -1.0f * Float.MAX_VALUE;
        for (Node n1: nn)
        {
            for (Node n2: nn)
            {
                if (n1 == n2) continue;
                float n1a = getArea(n1.getDimensions());
                float n2a = getArea(n2.getDimensions());
                float ja = 1.0f;
                for ( int i = 0; i < numDims; i++ )
                {
                    float jc0 = Math.min(n1.getCoords()[i], n2.getCoords()[i]);
                    float jc1 = Math.max(n1.getCoords()[i] + n1.getDimensions()[i], n2.getCoords()[i] + n2.getDimensions()[i]);
                    ja *= (jc1 - jc0);
                }
                float waste = ja - n1a - n2a;
                if ( waste > maxWaste )
                {
                    maxWaste = waste;
                    bestPair[0] = n1;
                    bestPair[1] = n2;
                }
            }
        }
        nn.remove(bestPair[0]);
        nn.remove(bestPair[1]);
        return bestPair;
    }

    /**
     * Implementation of QuadraticPickNext
     * @param cc the children to be divided between the new nodes, one item will be removed from this list.
     * @param nn the candidate nodes for the children to be added to.
     */
    private Node qPickNext(LinkedList<Node> cc, Node[] nn)
    {
        float maxDiff = -1.0f * Float.MAX_VALUE;
        Node nextC = null;
        for ( Node c: cc )
        {
            float n0Exp = getRequiredExpansion(nn[0].getCoords(), nn[0].getDimensions(), c);
            float n1Exp = getRequiredExpansion(nn[1].getCoords(), nn[1].getDimensions(), c);
            float diff = Math.abs(n1Exp - n0Exp);
            if (diff > maxDiff)
            {
                maxDiff = diff;
                nextC = c;
            }
        }
        assert (nextC != null) : "No node selected from qPickNext";
        cc.remove(nextC);
        return nextC;
    }

    // Implementation of LinearPickSeeds
    private Node[] lPickSeeds(LinkedList<Node> nn)
    {
        @SuppressWarnings("unchecked")
        Node[] bestPair = new Node[2];
        boolean foundBestPair = false;
        float bestSep = 0.0f;
        for (int i = 0; i < numDims; i++)
        {
            float dimLb = Float.MAX_VALUE, dimMinUb = Float.MAX_VALUE;
            float dimUb = -1.0f * Float.MAX_VALUE, dimMaxLb = -1.0f * Float.MAX_VALUE;
            Node nMaxLb = null, nMinUb = null;
            for (Node n : nn)
            {
                if (n.getCoords()[i] < dimLb)
                {
                    dimLb = n.getCoords()[i];
                }
                if (n.getDimensions()[i] + n.getCoords()[i] > dimUb)
                {
                    dimUb = n.getDimensions()[i] + n.getCoords()[i];
                }
                if (n.getCoords()[i] > dimMaxLb)
                {
                    dimMaxLb = n.getCoords()[i];
                    nMaxLb = n;
                }
                if (n.getDimensions()[i] + n.getCoords()[i] < dimMinUb)
                {
                    dimMinUb = n.getDimensions()[i] + n.getCoords()[i];
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
            bestPair = new Node[] { nn.get(0), nn.get(1) };
        }
        nn.remove(bestPair[0]);
        nn.remove(bestPair[1]);
        return bestPair;
    }

    /**
     * Implementation of LinearPickNext
     * @param cc the children to be divided between the new nodes, one item will be removed from this list.
     */
    private Node lPickNext(LinkedList<Node> cc)
    {
        return cc.pop();
    }

    private void tighten(Node... nodes)
    {
        assert(nodes.length >= 1): "Pass some nodes to tighten!";
        for (Node n: nodes) {
            assert(n.getChildren().size() > 0) : "tighten() called on empty node!";
            float[] minCoords = new float[numDims];
            float[] maxCoords = new float[numDims];
            for (int i = 0; i < numDims; i++)
            {
                minCoords[i] = Float.MAX_VALUE;
                maxCoords[i] = Float.MIN_VALUE;

                for (Node c : n.getChildren())
                {
                    // we may have bulk-added a bunch of children to a node (eg. in
                    // splitNode)
                    // so here we just enforce the child->parent relationship.
                    c.setParent(n);
                    if (c.getCoords()[i] < minCoords[i])
                    {
                        minCoords[i] = c.getCoords()[i];
                    }
                    if ((c.getCoords()[i] + c.getDimensions()[i]) > maxCoords[i])
                    {
                        maxCoords[i] = (c.getCoords()[i] + c.getDimensions()[i]);
                    }
                }
            }
            for (int i = 0; i < numDims; i++)
            {
                // Convert max coords to dimensions
                maxCoords[i] -= minCoords[i];
            }
            System.arraycopy(minCoords, 0, n.getCoords(), 0, numDims);
            System.arraycopy(maxCoords, 0, n.getDimensions(), 0, numDims);
        }
    }

    private Node chooseLeaf(Node n, Entry e)
    {
        if (n.isLeaf())
        {
            return n;
        }
        float minInc = Float.MAX_VALUE;
        Node next = null;
        for (Node c : n.getChildren())
        {
            float inc = getRequiredExpansion(c.getCoords(), c.getDimensions(), e);
            if (inc < minInc)
            {
                minInc = inc;
                next = c;
            }
            else if (inc == minInc)
            {
                float curArea = 1.0f;
                float thisArea = 1.0f;
                for (int i = 0; i < c.getDimensions().length; i++)
                {
                    curArea *= next.getDimensions()[i];
                    thisArea *= c.getDimensions()[i];
                }
                if (thisArea < curArea)
                {
                    next = c;
                }
            }
        }
        return chooseLeaf(next, e);
    }

    /**
     * Returns the increase in area necessary for the given rectangle to cover the
     * given entry.
     */
    private float getRequiredExpansion(float[] coords, float[] dimensions, Node e)
    {
        float area = getArea(dimensions);
        float[] deltas = new float[dimensions.length];
        for (int i = 0; i < deltas.length; i++)
        {
            if (coords[i] + dimensions[i] < e.getCoords()[i] + e.getDimensions()[i])
            {
                deltas[i] = e.getCoords()[i] + e.getDimensions()[i] - coords[i] - dimensions[i];
            }
            else if (coords[i] + dimensions[i] > e.getCoords()[i] + e.getDimensions()[i])
            {
                deltas[i] = coords[i] - e.getCoords()[i];
            }
        }
        float expanded = 1.0f;
        for (int i = 0; i < dimensions.length; i++)
        {
            expanded *= dimensions[i] + deltas[i];
        }
        return (expanded - area);
    }

    private float getArea(float[] dimensions)
    {
        float area = 1.0f;
        for (int i = 0; i < dimensions.length; i++)
        {
            area *= dimensions[i];
        }
        return area;
    }

    private boolean isOverlap(float[] scoords, float[] sdimensions,
                              float[] coords, float[] dimensions)
    {
        final float FUDGE_FACTOR=1.001f;
        for (int i = 0; i < scoords.length; i++)
        {
            boolean overlapInThisDimension = false;
            if (scoords[i] == coords[i])
            {
                overlapInThisDimension = true;
            }
            else if (scoords[i] < coords[i])
            {
                if (scoords[i] + FUDGE_FACTOR*sdimensions[i] >= coords[i])
                {
                    overlapInThisDimension = true;
                }
            }
            else if (scoords[i] > coords[i])
            {
                if (coords[i] + FUDGE_FACTOR*dimensions[i] >= scoords[i])
                {
                    overlapInThisDimension = true;
                }
            }
            if (!overlapInThisDimension)
            {
                return false;
            }
        }
        return true;
    }

    // The methods below this point can be used to create an HTML rendering
    // of the RTree.  Maybe useful for debugging?

    private static final int elemWidth = 150;
    private static final int elemHeight = 120;

    String visualize()
    {
        int ubDepth = (int)Math.ceil(Math.log(size)/Math.log(minEntries)) * elemHeight;
        int ubWidth = size * elemWidth;
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        pw.println( "<html><head></head><body>");
        visualize(store.getRootNode(), pw, 0, 0, ubWidth, ubDepth);
        pw.println( "</body>");
        pw.flush();
        return sw.toString();
    }

    private void visualize(Node n, java.io.PrintWriter pw, int x0, int y0, int w, int h)
    {
        pw.printf( "<div style=\"position:absolute; left: %d; top: %d; width: %d; height: %d; border: 1px dashed\">\n",
                x0, y0, w, h);
        pw.println( "<pre>");
        pw.println( "Node: " + n.toString() + " (root==" + (n == store.getRootNode()) + ") \n" );
        pw.println( "Coords: " + Arrays.toString(n.getCoords()) + "\n");
        pw.println( "Dimensions: " + Arrays.toString(n.getDimensions()) + "\n");
        pw.println( "# Children: " + ((n.getChildren() == null) ? 0 : n.getChildren().size()) + "\n" );
        pw.println( "isLeaf: " + n.isLeaf() + "\n");
        pw.println( "</pre>");
        int numChildren = (n.getChildren() == null) ? 0 : n.getChildren().size();
        for ( int i = 0; i < numChildren; i++ )
        {
            visualize(n.getChildren().get(i), pw, (int)(x0 + (i * w/(float)numChildren)),
                    y0 + elemHeight, (int)(w/(float)numChildren), h - elemHeight);
        }
        pw.println( "</div>" );
    }
}