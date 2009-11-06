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
package choco.cp.solver.constraints.global.multicostregular.structure.bitset;


import choco.cp.solver.constraints.global.multicostregular.structure.IArc;
import choco.cp.solver.constraints.global.multicostregular.structure.INode;
import choco.cp.solver.constraints.global.multicostregular.structure.AbstractArc;


/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Jul 17, 2008
 * Time: 10:48:56 AM
 *
 * Represent an Arc in the Layered Graph of the MCR propagator
 */
public class BArc extends AbstractArc {



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
    public final BNode orig;

    /**
     * Destination Node of the arc
     */
    public final BNode dest;




    /**
     * Construct a new Arc for a Layered graph
     * @param orig Origin node
     * @param dest Destination node
     * @param j Arc label
     */
    public BArc(final BNode orig, final BNode dest, final int j)
    {
        super(j);
        this.orig = orig;
        this.dest = dest;

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



    public boolean equals(Object o)
    {
        if (o instanceof IArc)
        {
            BArc e = (BArc) o;
            return j == e.j && outIndex == e.outIndex && inIndex == e.inIndex && orig.getLayer() == e.orig.getLayer();
        }
        return false;
    }

    public String toString()
    {
        return ""+ outIndex +" | "+j;
    }

    @Override
    public final INode getOrigin() {
        return orig;
    }

    @Override
    public final INode getDestination() {
        return dest;
    }

    @Override
    public final int getInStackIdx() {
        return this.getObjectIdx();
    }

    @Override
    public final int getObjectIdx() {
        return this.getOutIndex();
    }

    
}



