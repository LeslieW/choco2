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

import choco.cp.solver.constraints.global.multicostregular.structure.AbstractNode;


/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Jul 17, 2008
 * Time: 10:33:01 AM
 * Class to represent a Node in the layered graph of the Multi-Cost-Regular propagator
 */
public class BNode extends AbstractNode {


    /**
     *  where can the outgoing arcs be found in the layeredgraph bitset
     */
    protected int outOffset;

    /**
     * The number of outgoing arcs
     */
    protected int nbOutArcs;

    /**
     * where can the incomming arcs be found in the layeredgraph bitset
     */
    protected int inOffset;

    /**
     * The number of incomming arcs
     */
    protected int nbInArcs;




    /**
     * Create a new Node Instance
     * @param layer The layer the node will be put in
     * @param state The associated automaton state
     */
    public BNode(final int layer, final int state)
    {
        super(layer,state);
    }



    public final int getOutOffset() {
        return outOffset;
    }

    public final void setOutOffset(int outOffset) {
        this.outOffset = outOffset;
    }

    public final int getNbOutArcs() {
        return nbOutArcs;
    }

    public final void setNbOutArcs(int nbOutArcs) {
        this.nbOutArcs = nbOutArcs;
    }

    public final int getInOffset() {
        return inOffset;
    }

    public final void setInOffset(int inOffset) {
        this.inOffset = inOffset;
    }

    public final int getNbInArcs() {
        return nbInArcs;
    }

    public final void setNbInArcs(int nbInArcs) {
        this.nbInArcs = nbInArcs;
    }




}
