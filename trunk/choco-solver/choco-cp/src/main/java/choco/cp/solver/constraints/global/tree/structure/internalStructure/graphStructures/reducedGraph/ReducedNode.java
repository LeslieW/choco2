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
package choco.cp.solver.constraints.global.tree.structure.internalStructure.graphStructures.reducedGraph;


import java.util.BitSet;

public class ReducedNode {

    protected BitSet SCC;
    protected int pa;
    /**
    * constructor
    * @param scc une composante fortement connexe
    * @param v un sommet associe, dans le cas general un point d'articulation mais pas obligatoirement
    */
    public ReducedNode(int v,BitSet scc){
        pa = v;
        SCC = scc;
    }
    // accesseurs
    public int getAssociatedPA(){return pa;}
    public BitSet getSetVertex(){return SCC;}
}