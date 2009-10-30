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
package choco.cp.solver.constraints.global.multicostregular.structure.list;

import choco.cp.solver.constraints.global.multicostregular.structure.AbstractArc;
import choco.cp.solver.constraints.global.multicostregular.structure.INode;
import choco.kernel.memory.structure.IndexedObject;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jun 8, 2009
 * Time: 3:12:20 PM
 */
public class LArc extends AbstractArc implements IndexedObject {


    public int id;


    protected boolean flag;
    /**
     * Origin Node of the arc
     */
    public final LNode orig;

    /**
     * Destination Node of the arc
     */
    public final LNode dest;

    /**
     * Construct a new Arc for a Layered graph
     *
     * @param orig Origin node
     * @param dest Destination node
     * @param j    Arc label
     */

    protected int outIdx;
    protected int inIdx;

    public LArc(final LNode orig, final LNode dest, final int j, final int id) {
        super(j);
        this.id = id;
        this.flag = true;
        this.orig = orig;
        this.dest =dest;
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
        return id;
    }

    public final void setOutIdx(final int i)
    {
        this.outIdx = i;
    }
    public final void setInIdx(final int i)
    {
        this.inIdx = i;
    }

    @Override
    public final int getObjectIdx() {
        if (flag)
            return outIdx;
        else
            return inIdx;
    }

    public final void setInArc()
    {
        this.flag = false;
    }
    public final void setOutArc()
    {
        this.flag = true;
    }
}