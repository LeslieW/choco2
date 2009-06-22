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

import choco.cp.solver.constraints.global.tree.structure.internalStructure.graphStructures.reducedGraph.ReducedGraph;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.IntIterator;
import choco.kernel.memory.IStateBitSet;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class VarGraphView {

    protected final static Logger LOGGER = ChocoLogging.getSolverLogger();

    /**
     * boolean for debug and show a trace of the execution
     */
    protected boolean affiche = false;

    /**
     * Choco solver embedding the tree constraint
     */
    protected Solver solver;

    /**
     * the successor variables depicting the graph to partition
     */
    protected IntDomainVar[] s;

    /**
     * list of graph properties to maintain for the graph to partition
     */
    protected List<StoredBitSetGraph.Maintain> globalParams;

    /**
     * list of graph properties to maintain for the required graph to partition
     */
    protected List<StoredBitSetGraph.Maintain> sureParams;

    /**
     * list of graph properties to maintain for the potential graph to partition
     */
    protected List<StoredBitSetGraph.Maintain> maybeParams;

    /**
     * total number of nodes involved in the graph
     */
    protected int nbNodes;

    /**
     * data structure of the graph to partition
     */
    protected StoredBitSetGraph global;

    /**
     * data structure of the required graph associated with global
     */
    protected StoredBitSetGraph sure;

    /**
     * data structure of the potential graph associated with global
     */
    protected StoredBitSetGraph maybe;

    /**
     * backtrackable bitset matrix representing the graph to partition
     */
    protected IStateBitSet[] globalGraph;

    /**
     * backtrackable bitset matrix representing the required graph
     */
    protected IStateBitSet[] sureGraph;

    /**
     * backtrackable bitset matrix representing the potential graph
     */
    protected IStateBitSet[] maybeGraph;

    /**
     * reduced graph structure associated with global
     */
    protected ReducedGraph reducedGraph;

    /**
     * backtrackable bitset that store the potential roots involved in global
     */
    protected IStateBitSet potentialRoots;

    /**
     * Constructor of the graph view
     *
     * @param solver    the Choco solver who uses the current tree constraint
     * @param vars the successor variables depicting the graph to partition
     */
    public VarGraphView(Solver solver, IntDomainVar[] vars) {
        this.solver = solver;
        this.s = vars;
        this.nbNodes = vars.length;
        this.globalGraph = new IStateBitSet[nbNodes];
        this.sureGraph = new IStateBitSet[nbNodes];
        this.maybeGraph = new IStateBitSet[nbNodes];
        for (int i = 0; i < nbNodes; i++) {
            globalGraph[i] = solver.getEnvironment().makeBitSet(nbNodes);
            sureGraph[i] = solver.getEnvironment().makeBitSet(nbNodes);
            maybeGraph[i] = solver.getEnvironment().makeBitSet(nbNodes);
        }

        initGraphs();
        // announce the properties maintained
        this.globalParams = new ArrayList<StoredBitSetGraph.Maintain>();
        this.globalParams.add(StoredBitSetGraph.Maintain.TRANSITIVE_CLOSURE);
        this.sureParams = new ArrayList<StoredBitSetGraph.Maintain>();
        this.sureParams.add(StoredBitSetGraph.Maintain.TRANSITIVE_CLOSURE);
        this.sureParams.add(StoredBitSetGraph.Maintain.CONNECTED_COMP);
        this.maybeParams = new ArrayList<StoredBitSetGraph.Maintain>();
        this.maybeParams.add(StoredBitSetGraph.Maintain.TRANSITIVE_CLOSURE);
        // create graph associated with each view of the successor variables
        this.global = new StoredBitSetGraph(solver, globalGraph, globalParams, false);
        this.sure = new StoredBitSetGraph(solver, sureGraph, sureParams, false);
        this.maybe = new StoredBitSetGraph(solver, maybeGraph, maybeParams, false);
        // initialize the reduced graph
        this.reducedGraph = new ReducedGraph(solver, global);
        // compute potential roots
        this.potentialRoots = solver.getEnvironment().makeBitSet(nbNodes);
        for (int i = 0; i < nbNodes; i++) {
            if (globalGraph[i].get(i)) potentialRoots.set(i, true);
        }
    }

    private void initGraphs() {
        // initialize the required and the potential graphs
        for (int i = 0; i < nbNodes; i++) {
            if (s[i].isInstantiated()) {
                int j = s[i].getVal();
                sureGraph[i].set(j, true);
            } else {
                IntIterator it = s[i].getDomain().getIterator();
                while (it.hasNext()) {
                    int j = it.next();
                    maybeGraph[i].set(j, true);
                }
            }
        }
        for (int i = 0; i < nbNodes; i++) {
            IntIterator it = s[i].getDomain().getIterator();
            while (it.hasNext()) {
                int j = it.next();
                globalGraph[i].set(j, true);
            }
        }
    }

    /**
     * update strongly connected components
     */
    private void updateSCC() {
        reducedGraph.stronglyConnectedComponent();
    }

    /**
     * an arc (u,v) is removed from the graph
     *
     * @param u     index of a node
     * @param v     index of a node
     */
    public void updateOnRem(int u, int v) {
        maybe.remArc(u, v);
        global.remArc(u, v);
        this.updateSCC();
        if (u == v) potentialRoots.set(u, false);
    }

    /**
     * an arc (u,v) is fixed
     *
     * @param u     index of a node
     */
    public void updateOnInst(int u) {
        int v = s[u].getVal();
        // ajouter l'arc dans sure
        sure.addArc(u, v);
        // supprimer tous les succ de u dans maybe
        maybe.remAllSucc(u);
        // supprimer tous les succ de u dans global sauf v
        global.remAllExcepted(u, v);
        // mise � jour des scc
        this.updateSCC();
        // mettre � jour les racines potentielles
        if (u != v) potentialRoots.set(u, false);
    }

    /**
     * all the arc (u,v), such that v belongs to the set depicted by the iterator, are removed
     *
     * @param u     index of a node
     * @param deltaDomain   an iterator over the removed indices
     */
    public void updateOnRemovals(int u, IntIterator deltaDomain) {
        // supprimer tous les succ de u qui sont dans deltaDomain de maybe et global
        maybe.remAllNodes(u, deltaDomain);
        global.remAllNodes(u, deltaDomain);
        // mise � jour des scc
        this.updateSCC();
        while (deltaDomain.hasNext()) {
            int v = deltaDomain.next();
            if (u == v) potentialRoots.set(u, false);
        }
    }

    /**
     * remove all the successors of u with an index below to <code> s[u].getInf() </code>
     *
     * @param u     index of a node
     */
    public void updateOnInf(int u) {
        maybe.remAllLowerIdx(u, s[u].getInf());
        global.remAllLowerIdx(u, s[u].getInf());
        this.updateSCC();
        if (u < s[u].getInf()) potentialRoots.set(u, false);
    }

    /**
     * remove all the successors of u with an index higher than <code> s[u].getSup() </code>
     *
     * @param u     index of a node
     */
    public void updateOnSup(int u) {
        maybe.remAllGreaterIdx(u, s[u].getSup());
        global.remAllGreaterIdx(u, s[u].getSup());
        this.updateSCC();
        if (u > s[u].getSup()) potentialRoots.set(u, false);
    }

    /**
     * remove all the successors of u with an index below to <code> s[u].getInf() </code> and
     * higher than <code> s[u].getSup() </code>
     *
     * @param u     index of a node
     */
    public void updateOnBounds(int u) {
        maybe.remAllIdx(u, s[u].getInf(), s[u].getSup());
        global.remAllIdx(u, s[u].getInf(), s[u].getSup());
        this.updateSCC();
        if ((u < s[u].getInf()) || (s[u].getSup() < u)) potentialRoots.set(u, false);
    }

    /**
     *
     * @param u     index of a node
     * @return  <code> true </true> iff an outgoing arc from node u is fixed
     */
    public boolean isFixedSucc(int u) {
        return sure.getSuccessors(u).cardinality() > 0;
    }

    /**
     *
     * @param u     index of a node
     * @return  the index of the required successor of node u
     */
    public int getFixedSucc(int u) {
        return sure.getSuccessors(u).nextSetBit(0);
    }

    public int getNbNodes() {
        return nbNodes;
    }

    public Solver getSolver() {
        return solver;
    }

    public StoredBitSetGraph getGlobal() {
        return global;
    }

    public StoredBitSetGraph getSure() {
        return sure;
    }

    public StoredBitSetGraph getMaybe() {
        return maybe;
    }

    public IStateBitSet getPotentialRoots() {
        return potentialRoots;
    }

    public ReducedGraph getReducedGraph() {
        return reducedGraph;
    }

    public void showSure() {
        for (int i = 0; i < nbNodes; i++) {
            StringBuffer st = new StringBuffer();
            st.append("sure[").append(i).append("] = ");
            for (int j = sure.getGraph()[i].nextSetBit(0); j >= 0; j = sure.getGraph()[i].nextSetBit(j + 1)) {
                st.append(j).append(" ");
            }
            LOGGER.info(st.toString());
        }
    }

    public void showMaybe() {
        for (int i = 0; i < nbNodes; i++) {
            StringBuffer st = new StringBuffer();
            st.append("maybe[").append(i).append("] = ");
            for (int j = maybe.getGraph()[i].nextSetBit(0); j >= 0; j = maybe.getGraph()[i].nextSetBit(j + 1)) {
                st.append(j).append(" ");
            }
            LOGGER.info(st.toString());
        }
    }

    public void showGlobal() {
        for (int i = 0; i < nbNodes; i++) {
            StringBuffer st = new StringBuffer();
            st.append(MessageFormat.format("v{0}:= ", i));
            for (int j = global.getGraph()[i].nextSetBit(0); j >= 0; j = global.getGraph()[i].nextSetBit(j + 1)) {
                st.append(MessageFormat.format("{0} ", j));
            }
            LOGGER.info(st.toString());
        }
    }
}
