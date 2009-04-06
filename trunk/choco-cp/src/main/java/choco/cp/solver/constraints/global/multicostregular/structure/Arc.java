/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.constraints.global.multicostregular.structure;


import java.util.Comparator;


/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Jul 17, 2008
 * Time: 10:48:56 AM
 *
 * Represent an Arc in the Layered Graph of the MCR propagator
 */
public class Arc implements Comparable<Arc>{

    /**
     * Label carried by this Edge
     */
    protected final int j;

    /**
     * Index in the outgoing arcs array
     */
    protected int outIndex;

    /**
     * Index in the incomming arcs array
     */
    protected int inIndex;

    /**
     * Origin Node of the arc
     */
    public final Node orig;

    /**
     * Destination Node of the arc
     */
    public final Node dest;

    /**
     * static instance of an outgoing arcs comparator
     */
    public static final OutComparator outComparator = new OutComparator();



    /**
     * Construct a new Arc for a Layered graph
     * @param orig Origin node
     * @param dest Destination node
     * @param j Arc label
     */
    public Arc(final Node orig, final Node dest, final int j)
    {
        this.orig = orig;
        this.dest = dest;
        this.j = j;
    }

    public final int getLabel()
    {
        return j;
    }

    public final int getOutIndex() {
        return outIndex;
    }

    public final void setOutIndex(int outIndex) {
        this.outIndex = outIndex;
    }

    public final int getInIndex() {
        return inIndex;
    }

    public final void setInIndex(int inIndex) {
        this.inIndex = inIndex;
    }

    public int compareTo(final Arc edge) {
        return (this.dest.id < edge.dest.id) ? -1 : (this.dest.id == edge.dest.id) ? 0 : 1;
    }

    public boolean equals(Object o)
    {
        if (o instanceof Arc)
        {
            Arc e = (Arc) o;
            return j == e.j && outIndex == e.outIndex && inIndex == e.inIndex && orig.layer == e.orig.layer;
        }
        return false;
    }

    /**
     * Comparator of Outgoing arcs
     */
    public static class OutComparator implements Comparator<Arc> {



        public int compare(Arc edge, Arc edge1)
        {
            return (edge.orig.id < edge1.orig.id) ? -1 : (edge.orig.id == edge1.orig.id) ? 0 : 1;
        }
    }
    public String toString()
    {
        return ""+ outIndex +" | "+j;
    }
}


