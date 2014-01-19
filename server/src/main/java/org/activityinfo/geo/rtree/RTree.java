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
//    public enum SeedPicker { LINEAR, QUADRATIC }
//
//    private final int maxEntries;
//    private final int minEntries;
//    private final int numDims;
//
//    private final float[] pointDims;
//
//    private final SeedPicker seedPicker;
//
//    private Store store;
//
//    private volatile int size;
//
//    /**
//     * Creates a new RTree.
//     *
//     * @param maxEntries
//     *          maximum number of entries per node
//     * @param minEntries
//     *          minimum number of entries per node (except for the root node)
//     * @param numDims
//     *          the number of dimensions of the RTree.
//     */
//    public RTree(Store store, int maxEntries, int minEntries, int numDims, SeedPicker seedPicker)
//    {
//        assert (minEntries <= (maxEntries / 2));
//        this.store = store;
//        this.numDims = numDims;
//        this.maxEntries = maxEntries;
//        this.minEntries = minEntries;
//        this.seedPicker = seedPicker;
//        pointDims = new float[numDims];
//        if(store.getRootNode() == null) {
//            store.setRootNode(buildRoot(true));
//        }
//    }
//
//
//    private Node buildRoot(boolean asLeaf)
//    {
//        float[] initCoords = new float[numDims];
//        float[] initDimensions = new float[numDims];
//        for (int i = 0; i < this.numDims; i++)
//        {
//            initCoords[i] = (float) Math.sqrt(Float.MAX_VALUE);
//            initDimensions[i] = -2.0f * (float) Math.sqrt(Float.MAX_VALUE);
//        }
//        return store.createNode(initCoords, initDimensions, asLeaf);
//    }
//
//    /**
//     * Builds a new RTree using default parameters: maximum 50 entries per node
//     * minimum 2 entries per node 2 dimensions
//     */
//    public RTree(Store store)
//    {
//        this(store, 50, 2, 2, SeedPicker.LINEAR);
//    }
//
//    /**
//     * @return the maximum number of entries per node
//     */
//    public int getMaxEntries()
//    {
//        return maxEntries;
//    }
//
//    /**
//     * @return the minimum number of entries per node for all nodes except the
//     *         root.
//     */
//    public int getMinEntries()
//    {
//        return minEntries;
//    }
//
//    /**
//     * @return the number of dimensions of the tree
//     */
//    public int getNumDims()
//    {
//        return numDims;
//    }
//
//    /**
//     * @return the number of items in this tree.
//     */
//    public int size()
//    {
//        return size;
//    }
//
//    /**
//     * Searches the RTree for objects overlapping with the given rectangle.
//     *
//     * @param coords
//     *          the corner of the rectangle that is the lower bound of every
//     *          dimension (eg. the top-left corner)
//     * @param dimensions
//     *          the dimensions of the rectangle.
//     * @return a list of objects whose rectangles overlap with the given
//     *         rectangle.
//     */
//    public List<Data> search(float[] coords, float[] dimensions)
//    {
//        assert (coords.length == numDims);
//        assert (dimensions.length == numDims);
//        LinkedList<Data> results = new LinkedList<Data>();
//        search(coords, dimensions, store.getRootNode(), results);
//        return results;
//    }
//
//    private void search(float[] coords, float[] dimensions, Node n,
//                        LinkedList<Data> results)
//    {
//        if (n.isLeaf())
//        {
//            for (Node e : n.getChildren())
//            {
//                if (RectUtils.isOverlap(coords, dimensions, e.getCoords(), e.getDimensions()))
//                {
//                    results.add(((Entry) e).entry);
//                }
//            }
//        }
//        else
//        {
//            for (Node c : n.getChildren())
//            {
//                if (RectUtils.isOverlap(coords, dimensions, c.getCoords(), c.getDimensions()))
//                {
//                    search(coords, dimensions, c, results);
//                }
//            }
//        }
//    }
//
//    /**
//     * Deletes the entry associated with the given rectangle from the RTree
//     *
//     * @param coords
//     *          the corner of the rectangle that is the lower bound in every
//     *          dimension
//     * @param dimensions
//     *          the dimensions of the rectangle
//     * @param entry
//     *          the entry to delete
//     * @return true iff the entry was deleted from the RTree.
//     */
//    public boolean delete(float[] coords, float[] dimensions, Data entry)
//    {
//        assert (coords.length == numDims);
//        assert (dimensions.length == numDims);
//        Node l = findLeaf(store.getRootNode(), coords, dimensions, entry);
//        if ( l == null ) {
//            System.out.println("WTF?");
//            findLeaf(store.getRootNode(), coords, dimensions, entry);
//        }
//        assert (l != null) : "Could not find leaf for entry to delete";
//        assert (l.isLeaf()) : "Entry is not found at leaf?!?";
//        ListIterator<Node> li = l.getChildren().listIterator();
//        Data removed = null;
//        while (li.hasNext())
//        {
//            @SuppressWarnings("unchecked")
//            Entry e = (Entry) li.next();
//            if (e.entry.equals(entry))
//            {
//                removed = e.entry;
//                li.remove();
//                break;
//            }
//        }
//        if (removed != null)
//        {
//            condenseTree(l);
//            size--;
//        }
//        if ( size == 0 )
//        {
//            store.setRootNode(buildRoot(true));
//        }
//        return (removed != null);
//    }
//
//    public boolean delete(float[] coords, Data entry)
//    {
//        return delete(coords, pointDims, entry);
//    }
//
//    private Node findLeaf(Node n, float[] coords, float[] dimensions, Data entry)
//    {
//        if (n.isLeaf())
//        {
//            for (Node c : n.getChildren())
//            {
//                if (((Entry) c).entry.equals(entry))
//                {
//                    return n;
//                }
//            }
//            return null;
//        }
//        else
//        {
//            for (Node c : n.getChildren())
//            {
//                if (isOverlap(c.getCoords(), c.getDimensions(), coords, dimensions))
//                {
//                    Node result = findLeaf(c, coords, dimensions, entry);
//                    if (result != null)
//                    {
//                        return result;
//                    }
//                }
//            }
//            return null;
//        }
//    }
//
//    private void condenseTree(Node n)
//    {
//        Set<Node> q = new HashSet<Node>();
//        while (n != store.getRootNode())
//        {
//            if (n.isLeaf() && (n.getChildren().size() < minEntries))
//            {
//                q.addAll(n.getChildren());
//                n.getParent().getChildren().remove(n);
//            }
//            else if (!n.isLeaf() && (n.getChildren().size() < minEntries))
//            {
//                // probably a more efficient way to do this...
//                LinkedList<Node> toVisit = new LinkedList<Node>(n.getChildren());
//                while (!toVisit.isEmpty())
//                {
//                    Node c = toVisit.pop();
//                    if (c.isLeaf())
//                    {
//                        q.addAll(c.getChildren());
//                    }
//                    else
//                    {
//                        toVisit.addAll(c.getChildren());
//                    }
//                }
//                n.getParent().getChildren().remove(n);
//            }
//            else
//            {
//                tighten(n);
//            }
//            n = n.getParent();
//        }
//        if ( store.getRootNode().getChildren().size() == 0 )
//        {
//            store.setRootNode(buildRoot(true));
//        }
//        else if ( (store.getRootNode().getChildren().size() == 1) && (!store.getRootNode().isLeaf()) )
//        {
//            store.setRootNode(store.getRootNode().getChildren().get(0));
//            store.getRootNode().setParent(null);
//        }
//        else
//        {
//            tighten(store.getRootNode());
//        }
//        for (Node ne : q)
//        {
//            @SuppressWarnings("unchecked")
//            Entry e = (Entry) ne;
//            insert(e.getCoords(), e.getDimensions(), e.entry);
//        }
//        size -= q.size();
//    }
//
//    /**
//     * Empties the RTree
//     */
//    public void clear()
//    {
//        store.setRootNode(buildRoot(true));
//        // let the GC take care of the rest.
//    }
//
//    // The methods below this point can be used to create an HTML rendering
//    // of the RTree.  Maybe useful for debugging?
//
//    private static final int elemWidth = 150;
//    private static final int elemHeight = 120;
//
//    public String visualize()
//    {
//        int ubDepth = (int)Math.ceil(Math.log(size)/Math.log(minEntries)) * elemHeight;
//        int ubWidth = size * elemWidth;
//        java.io.StringWriter sw = new java.io.StringWriter();
//        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
//        pw.println( "<html><head></head><body><svg width=\"300\" height=\"200\" " +
//                "viewBox=\"" + viewBox(this.store.getRootNode()) + "\">");
//        visualize(store.getRootNode(), pw);
//        pw.println( "</svg></body>");
//        pw.flush();
//        return sw.toString();
//    }
//
//    private String viewBox(Node n) {
//        return String.format("%f %f %f %f", n.getCoords()[0], n.getCoords()[1], n.getDimensions()[0], n.getDimensions()[1]);
//    }
//
//    private void visualize(Node n, java.io.PrintWriter pw)
//    {
//
////
////        pw.println("<pre>");
////        pw.println( "Node: " + n.toString() + " (root==" + (n == store.getRootNode()) + ") \n" );
////        pw.println( "Coords: " + Arrays.toString(n.getCoords()) + "\n");
////        pw.println( "Dimensions: " + Arrays.toString(n.getDimensions()) + "\n");
////        pw.println( "# Children: " + ((n.getChildren() == null) ? 0 : n.getChildren().size()) + "\n" );
////        pw.println( "isLeaf: " + n.isLeaf() + "\n");
////        pw.println( "</pre>");
////        int numChildren = (n.getChildren() == null) ? 0 : n.getChildren().size();
//        for ( Node node : n.getChildren() ) {
//           visualize(node, pw);
//        }
//        pw.printf(String.format("<rect x=\"%f\" y=\"%f\" width=\"%f\" height=\"%f\" style=\"%s\" title=\"%s\"/>\n",
//                n.getCoords()[0], n.getCoords()[1], n.getDimensions()[0], n.getDimensions()[1],
//                style(n), n.toString()));
//    }
//
//
//    private String style(Node n) {
//        if(n instanceof Entry) {
//            return "fill: red; fill-opacity: 0.30; stroke: none; ";
//        } else {
//            return "fill: none; stroke: blue; stroke-dasharray: 1,1;";
//        }
//    }
}