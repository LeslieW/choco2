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
package choco.cp.solver.constraints.global.tree.structure.internalStructure.graphStructures.graphViews;

/* ************************************************
 *           _       _                            *
 *          |  �(..)  |                           *
 *          |_  J||L _|       Choco-Solver.net    *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien   1999-2008       *
 **************************************************/

import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.tree.structure.inputStructure.Node;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.memory.trailing.StoredBitSet;
import choco.kernel.solver.Solver;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class PrecsGraphView {

    /**
     * boolean for debug and show a trace of the execution
     */
    protected boolean affiche = false;

    /**
     * Choco solver embedding the tree constraint
     */
    protected Solver solver;

    /**
     * list of graph properties to maintain for the precedence graph
     */
    protected List<StoredBitSetGraph.Maintain> precsParams;

    /**
     * total number of nodes involved in the graph
     */
    protected int nbNodes;

    /**
     * data structure of the precedence graph
     */
    protected StoredBitSetGraph precs;

    /**
     * backtrackable bitset matrix representing the precedence graph
     */
    protected StoredBitSet[] precsGraph;

    /**
     * 
     * @param solver    the Choco solver who uses the current tree constraint
     * @param nodes     total number of nodes involved in the graph
     */
    public PrecsGraphView(Solver solver, Node[] nodes) {
        this.solver = solver;
        this.nbNodes = nodes.length;
        // announce the properties maintained
        this.precsParams = new ArrayList<StoredBitSetGraph.Maintain>();
        this.precsParams.add(StoredBitSetGraph.Maintain.TRANSITIVE_CLOSURE);
        this.precsParams.add(StoredBitSetGraph.Maintain.TRANSITIVE_REDUCTION);
        this.precsParams.add(StoredBitSetGraph.Maintain.CONNECTED_COMP);
        this.precsGraph = new StoredBitSet[nbNodes];
        for (int i = 0; i < nbNodes; i++) this.precsGraph[i] = nodes[i].getMandatorySuccessors();
        // create the precedence graph associated with the precedence constraints
        this.precs = new StoredBitSetGraph(solver, this.precsGraph, this.precsParams, false);
    }

    /**
     * incrementaly add the arc (u,v) in the precedence graph
     *
     * @param u     index of a node
     * @param v     index of a node
     * @return  <code> true </code> iff the arc (u,v) is effectively added in the precedence graph
     */
    public boolean addPrec(int u, int v) {
        if (affiche) {
        	ChocoLogging.flushLogs();
            System.out.println("============= Add Incr�mental : (" + u + "," + v + ") ================");
            precs.showGraph("precs");
            System.out.println("---------------");
            precs.showAllDesc("tcPrecs");
            System.out.println("**********************");
        }
        boolean res = false;
        if (u != v) {
            if (!precs.getDescendants(u).get(v)) {
                if (affiche)
                    System.out.println("\t\t(" + u + "," + v + ") est ajoute dans Gp!");
                precs.addArc(u, v);
                // transitive reduction become the current view of the precedence graph
                this.precsGraph = this.precs.getTrGraph();
                res = true;
            } else {
                if (affiche) System.out.println("\t\t(" + u + "," + v + ") a deja ete ajoute dans Gp");
                res = false;
            }
        }
        if (affiche) {
            precs.showGraph("precs");
            System.out.println("---------------");
            precs.showAllDesc("tcPrecs");
            System.out.println("============= END Add Incr�mental ================");
        }
        return res;
    }

    public StoredBitSetGraph getPrecs() {
        return precs;
    }

    public StoredBitSet getSuccessors(int i) {
        return precs.getSuccessors(i);
    }

    public StoredBitSet getPredecessors(int i) {
        return precs.getPredecessors(i);
    }

    public BitSet getDescendants(int i) {
        return hardCopy(precs.getDescendants(i));
    }

    public BitSet getAncestors(int i) {
        return hardCopy(precs.getAncestors(i));
    }

    public StoredBitSet getSinkNodes() {
        return precs.getSinkNodes();
    }

    public StoredBitSet getSrcNodes() {
        return precs.getSrcNodes();
    }

    public StoredBitSet[] getVertFromNumCC() {
        return precs.getVertFromNumCC();
    }

    public StoredBitSet[] getNumFromVertCC() {
        return precs.getNumFromVertCC();
    }

    public String showDesc(int i) {
        return precs.showDesc(i, "descPrecs");
    }

    public void showPrecGraph() {
        precs.showGraph("precs");
    }

    public void showAllDesc() {
        precs.showAllDesc("descPrecs");
    }

    private BitSet hardCopy(StoredBitSet b) {
        BitSet bs = new BitSet(nbNodes);
        for (int i = b.nextSetBit(0); i >= 0; i = b.nextSetBit(i + 1)) {
            bs.set(i, true);
        }
        return bs;
    }

}
