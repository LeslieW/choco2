package choco.cp.solver.constraints.global.multicostregular.structure;

import choco.kernel.memory.trailing.IndexedObject;

import java.util.Comparator;/* * * * * * * * * * * * * * * * * * * * * * * * *
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

public interface IArc extends Comparable<IArc>, IndexedObject {
    /**
     * static instance of an outgoing arcs comparator
     */
    OutComparator outComparator = new OutComparator();

    int getLabel();


    int compareTo(IArc edge);

    boolean equals(Object o);

    String toString();

    INode getOrigin();
    INode getDestination();

    int getInStackIdx();

    /**
     * Comparator of Outgoing arcs
     */
    public static class OutComparator implements Comparator<IArc> {



        public int compare(IArc edge, IArc edge1)
        {
            return (edge.getOrigin().getId() < edge1.getOrigin().getId()) ? -1 : (edge.getOrigin().getId() == edge1.getOrigin().getId()) ? 0 : 1;
        }
    }
}
