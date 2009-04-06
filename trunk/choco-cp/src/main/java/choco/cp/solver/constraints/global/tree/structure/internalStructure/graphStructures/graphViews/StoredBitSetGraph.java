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


import choco.cp.solver.constraints.global.tree.structure.internalStructure.graphStructures.algorithms.ConnectedComponents;
import choco.kernel.common.util.IntIterator;
import choco.kernel.memory.IStateInt;
import choco.kernel.memory.trailing.StoredBitSet;
import choco.kernel.solver.Solver;

import java.util.List;
import java.util.Stack;
import java.util.Vector;

/**
 * the core class that allow to represent a graph and a set of properties that can be dynamically maintained.
 */
public class StoredBitSetGraph {

    /**
     * list of graph properties that can be maintained for a given graph
     */
    public static enum Maintain {
        TRANSITIVE_CLOSURE, TRANSITIVE_REDUCTION, CONNECTED_COMP, NONE
    }

    /**
     * Choco solver embedding the tree constraint
     */
    protected Solver solver;

    /**
     * list of graph properties to maintain for the graph
     */
    protected List<Maintain> params;

    /**
     * total number of nodes involved in the graph
     */
    protected int nbNodes;

    /**
     * reference idx in a depth first search
     */
    protected int idx;

    /**
     * resulting labelling of the nodes involved in the graph according to a depth first search 
     */
    protected int[] dfsTree;

    /**
     * backtrackable bitset matrix representing the graph
     */
    protected StoredBitSet[] graph;

    /**
     * backtrackable bitset matrix representing the reverse graph
     */
    protected StoredBitSet[] revGraph;

    /**
     * backtrackable bitset matrix representing the transitive closure of the graph
     */
    protected StoredBitSet[] tcGraph;

    /**
     * backtrackable bitset matrix representing the reverse transitive closure of the graph
     */
    protected StoredBitSet[] revTcGraph;

    protected boolean needUpdate;

    /**
     * backtrackable bitset matrix representing the transitive reduction of the graph
     */
    protected StoredBitSet[] trGraph;
    
    /**
     * backtrackable bitset matrix representing the reverse transitive reduction of the graph
     */
    protected StoredBitSet[] revTrGraph;

    /**
     * backtrackable bitset that store the source nodes of the graph
     */
    protected StoredBitSet srcNodes;

    /**
     * backtrackable bitset that store the sink nodes of the graph
     */
    protected StoredBitSet sinkNodes;

    /**
     * connected component structure associated with the graph
     */
    protected ConnectedComponents cc;
    protected Vector<StoredBitSet> setCC;
    protected StoredBitSet[] vertFromNumCC;
    protected StoredBitSet[] numFromVertCC;

    /**
     * backtrackable integer recording the current number of connected components
     */
    protected IStateInt nbCC;

    /**
     * boolean for debug and show a trace of the execution
     */
    protected boolean affiche;

    public StoredBitSetGraph(Solver solver, StoredBitSet[] graph, List<Maintain> params, boolean affiche) {
        this.solver = solver;
        this.graph = graph;
        this.params = params;
        this.nbNodes = graph.length;
        this.affiche = affiche;
        this.idx = 0;
        this.dfsTree = new int[nbNodes];
        for (int k = 0; k < nbNodes; k++) dfsTree[k] = -1;
        // initialize the set of source and sink nodes of the graph
        this.srcNodes = new StoredBitSet(solver.getEnvironment(), nbNodes);
        this.sinkNodes = new StoredBitSet(solver.getEnvironment(), nbNodes);
        // initialize the required graph associated with the initial one
        this.revGraph = new StoredBitSet[nbNodes];
        if (params.contains(Maintain.TRANSITIVE_CLOSURE)) {
            this.tcGraph = new StoredBitSet[nbNodes];
            this.revTcGraph = new StoredBitSet[nbNodes];
        } else {
            this.tcGraph = null;
            this.revTcGraph = null;
        }
        if (params.contains(Maintain.TRANSITIVE_REDUCTION)) {
            this.trGraph = new StoredBitSet[nbNodes];
            this.revTrGraph = new StoredBitSet[nbNodes];
        } else {
            this.trGraph = null;
            this.revTrGraph = null;
        }
        // initialize internal data structure of the graphs
        initAllGraphs();
        createRevGraph();
        updateSpecialNodes();
        // compute required properties
        if (params.contains(Maintain.TRANSITIVE_CLOSURE)) computeTCfromScratch();
        if (params.contains(Maintain.TRANSITIVE_REDUCTION)) computeTRfromScratch();
        if (params.contains(Maintain.CONNECTED_COMP)) {
            initCCstruct();
            computeCCfromScratch();
        }
    }

    private void initCCstruct() {
        this.setCC = new Vector<StoredBitSet>(this.nbNodes);
        this.vertFromNumCC = new StoredBitSet[this.nbNodes];
        this.numFromVertCC = new StoredBitSet[this.nbNodes];
        for (int i = 0; i < this.nbNodes; i++) {
            this.setCC.add(new StoredBitSet(this.solver.getEnvironment(), this.nbNodes));
            this.vertFromNumCC[i] = new StoredBitSet(this.solver.getEnvironment(), this.nbNodes);
            this.numFromVertCC[i] = new StoredBitSet(this.solver.getEnvironment(), this.nbNodes);
        }
        this.nbCC = this.solver.getEnvironment().makeInt(0);
        this.cc = new ConnectedComponents(this.solver, this.nbNodes, this.graph, this.setCC);
    }

    private void initAllGraphs() {
        for (int i = 0; i < nbNodes; i++) {
            this.revGraph[i] = new StoredBitSet(solver.getEnvironment(), nbNodes);
            if (params.contains(Maintain.TRANSITIVE_CLOSURE)) {
                this.tcGraph[i] = new StoredBitSet(solver.getEnvironment(), nbNodes);
                this.revTcGraph[i] = new StoredBitSet(solver.getEnvironment(), nbNodes);
            }
            if (params.contains(Maintain.TRANSITIVE_REDUCTION)) {
                this.trGraph[i] = new StoredBitSet(solver.getEnvironment(), nbNodes);
                this.revTrGraph[i] = new StoredBitSet(solver.getEnvironment(), nbNodes);
            }
        }
    }

    private void createRevGraph() {
        for (int i = 0; i < nbNodes; i++) {
            for (int j = graph[i].nextSetBit(0); j >= 0; j = graph[i].nextSetBit(j + 1)) {
                revGraph[j].set(i, true);
            }
        }
    }

    private void computeTCfromScratch() {
        razTC();
        for (int i = 0; i < nbNodes; i++) {
            for (int j = graph[i].nextSetBit(0); j >= 0; j = graph[i].nextSetBit(j + 1)) {
                if (i != j) {
                    tcGraph[i].set(j, true);
                    revTcGraph[j].set(i, true);
                }
            }
        }
        for (int i = 0; i < nbNodes; i++) {
            for (int j = 0; j < nbNodes; j++) {
                if (tcGraph[j].get(i) && j != i) {
                    for (int k = tcGraph[i].nextSetBit(0); k >= 0; k = tcGraph[i].nextSetBit(k + 1)) {
                        tcGraph[j].set(k, true);
                        revTcGraph[k].set(j, true);
                    }
                }
            }
        }
    }

    private void addIncreTC(int i, int j) {
        if (i != j) {
            // descendants
            if (!tcGraph[i].get(j)) {
                tcGraph[i].or(tcGraph[j]);
                tcGraph[i].set(j, true);
                for (int k = revTcGraph[i].nextSetBit(0); k >= 0; k = revTcGraph[i].nextSetBit(k + 1)) {
                    if (!tcGraph[k].get(j)) {
                        tcGraph[k].or(tcGraph[j]);
                        tcGraph[k].set(j, true);
                    }
                }
                // ancestors
                revTcGraph[j].or(revTcGraph[i]);
                revTcGraph[j].set(i, true);
                for (int k = tcGraph[j].nextSetBit(0); k >= 0; k = tcGraph[j].nextSetBit(k + 1)) {
                    if (!tcGraph[i].get(k)) {
                        revTcGraph[k].or(revTcGraph[i]);
                        revTcGraph[k].set(i, true);
                    }
                }
            }
        }
    }

    private void remIncreTC(int i, int j) {
        if (i != j) {
            // reachable nodes from node i in the graph
            StoredBitSet tempDesc = getDesc(i, j, graph);
            if (needUpdate) {
                tcGraph[i] = tempDesc;
                // compute all the reachble nodes from each ancestor of i in graph
                StoredBitSet updateAnc = new StoredBitSet(solver.getEnvironment(), nbNodes);
                for (int k = revTcGraph[i].nextSetBit(0); k >= 0; k = revTcGraph[i].nextSetBit(k + 1)) {
                    if (!updateAnc.get(k)) {
                        tempDesc = getDesc(k, j, graph);
                        if (!needUpdate) updateAnc.or(revTcGraph[k]);
                        else tcGraph[k] = tempDesc;
                    }
                }
                // compute the nodes reachable from j in the reverse graph
                revTcGraph[j] = getDesc(j, i, revGraph);
                // compute all the nodes reachable from each descendant of j in the reverse graph
                StoredBitSet updateDesc = new StoredBitSet(solver.getEnvironment(), nbNodes);
                for (int k = tcGraph[j].nextSetBit(0); k >= 0; k = tcGraph[j].nextSetBit(k + 1)) {
                    if (!updateDesc.get(k)) {
                        tempDesc = getDesc(k, i, revGraph);
                        if (!needUpdate) updateDesc.or(tcGraph[k]);
                        else revTcGraph[k] = tempDesc;
                    }
                }
            }
        }
    }

    private StoredBitSet getDesc(int i, int j, StoredBitSet[] graph) {
        // retrieve the set of reachable nodes from i in the graph
        needUpdate = true;
        Stack<Integer> stack = new Stack<Integer>();
        StoredBitSet reached = new StoredBitSet(solver.getEnvironment(), nbNodes);
        stack.push(i);
        while (!stack.isEmpty()) {
            int a = stack.pop();
            for (int b = graph[a].nextSetBit(0); b >= 0; b = graph[a].nextSetBit(b + 1)) {
                if (!stack.contains(b) && !reached.get(b)) {
                    reached.set(b, true);
                    if (b == j) {
                        needUpdate = false;
                        return reached;
                    } else stack.push(b);
                }
            }
        }
        return reached;
    }

    private void razTC() {
        for (int i = 0; i < nbNodes; i++) {
            for (int j = tcGraph[i].nextSetBit(0); j >= 0; j = tcGraph[i].nextSetBit(j + 1)) {
                tcGraph[i].set(j, false);
                revTcGraph[j].set(i, false);
            }
        }
    }

    private void computeTRfromScratch() {
        razTR();
        for (int i = 0; i < nbNodes; i++) {
            for (int j = graph[i].nextSetBit(0); j >= 0; j = graph[i].nextSetBit(j + 1)) {
                trGraph[i].set(j, true);
                revTrGraph[j].set(i, true);
            }
        }
        for (int i = 0; i < nbNodes; i++) {
            int[][] num = new int[nbNodes][2];
            for (int j = 0; j < nbNodes; j++) {
                num[j][0] = -1;
                num[j][1] = -1;
            }
            idx = 0;
            for (int k = 0; k < nbNodes; k++) dfsTree[k] = -1;
            dfs(i, i, num);
        }
    }

    private void razTR() {
        for (int i = 0; i < nbNodes; i++) {
            for (int j = trGraph[i].nextSetBit(0); j >= 0; j = trGraph[i].nextSetBit(j + 1)) {
                trGraph[i].set(j, false);
                revTrGraph[j].set(i, false);
            }
        }
    }

    private int[][] dfs(int root, int u, int[][] num) {
        num[u][0] = idx++;
        for (int v = graph[u].nextSetBit(0); v >= 0; v = graph[u].nextSetBit(v + 1)) {
            if (num[v][0] == -1) {
                dfsTree[v] = u;
                num = dfs(root, v, num);
            } else {
                if (num[u][1] == -1 && num[u][0] > num[v][0]) {
                    int w = dfsTree[v];
                    if (w == root) { // (w,v) is a transitive arc in the dfs tree
                        trGraph[w].set(v, false);
                        revTrGraph[v].set(w, false);
                    }
                }
                // (u,v) is a transitive arc in a specific branch of the dfs tree
                if (num[v][1] != -1 && num[u][0] < num[v][0]) {
                    trGraph[u].set(v, false);
                    revTrGraph[v].set(u, false);
                }
            }
        }
        num[u][1] = idx++;
        return num;
    }

    private void computeCCfromScratch() {
        this.cc.getConnectedComponents(affiche);
        if (affiche) showCC();
        // record the connected components of the graph
        for (int i = 0; i < nbNodes; i++) this.numFromVertCC[i].clear();
        for (int i = 0; i < this.setCC.size(); i++) {
            StoredBitSet contain = this.setCC.elementAt(i);
            this.vertFromNumCC[i].clear();
            for (int j = contain.nextSetBit(0); j >= 0; j = contain.nextSetBit(j + 1)) {
                this.vertFromNumCC[i].set(j, true);
                this.numFromVertCC[j].set(i, true);
            }
        }
        this.nbCC.set(this.cc.getNbCC());
    }

    private void showCC() {
        for (int i = 0; i < setCC.size(); i++) {
            StoredBitSet contain = setCC.elementAt(i);
            System.out.print("cc(" + i + ") = " + contain.toString());
            System.out.println("");
        }
        System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*");
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    ////////////////// Algorithmes pour ajouter/retirer un arc dans graph ////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////

    /**
     * add the arc (u,v) in the graph view structure (required properties are dynamically updated)
     *
     * @param u     index of a node
     * @param v     index of a node
     */
    public void addArc(int u, int v) {
        // add arc
        graph[u].set(v, true);
        revGraph[v].set(u, true);
        // update properties
        if (params.contains(Maintain.TRANSITIVE_CLOSURE)) addIncreTC(u, v);
        if (params.contains(Maintain.TRANSITIVE_REDUCTION)) computeTRfromScratch();
        // update sink and source informations
        updateSpecialNodes(u, v);
        // update connected components
        if (params.contains(Maintain.CONNECTED_COMP)) computeCCfromScratch();
    }

    /**
     * remove the arc (u,v) from the graph view structure (required properties are dynamically updated)
     *
     * @param u     index of a node
     * @param v     index of a node
     */
    public void remArc(int u, int v) {
        // remove arc
        graph[u].set(v, false);
        revGraph[v].set(u, false);
        // update properties
        if (params.contains(Maintain.TRANSITIVE_CLOSURE)) remIncreTC(u, v);
        if (params.contains(Maintain.TRANSITIVE_REDUCTION)) computeTRfromScratch();
        // update sink and source informations
        updateSpecialNodes(u, v);
        // update connected components
        if (params.contains(Maintain.CONNECTED_COMP)) computeCCfromScratch();
    }

    /**
     * remove all the successors of node u from the graph view structure (required properties are
     * dynamically updated)
     *
     * @param u     index of a node
     */
    public void remAllSucc(int u) {
        for (int v = graph[u].nextSetBit(0); v >= 0; v = graph[u].nextSetBit(v + 1)) {
            graph[u].set(v, false);
            revGraph[v].set(u, false);
        }
        for (int v = graph[u].nextSetBit(0); v >= 0; v = graph[u].nextSetBit(v + 1)) {
            if (params.contains(Maintain.TRANSITIVE_CLOSURE)) remIncreTC(u, v);
        }
        if (params.contains(Maintain.TRANSITIVE_REDUCTION)) computeTRfromScratch();
        updateSpecialNodes();
        if (params.contains(Maintain.CONNECTED_COMP)) computeCCfromScratch();
    }

    /**
     * remove all the successors of node u, excepted node v, from the graph view structure (required properties are
     * dynamically updated)
     *
     * @param u     index of a node
     * @param v     index of a node
     */
    public void remAllExcepted(int u, int v) {
        // remove all the outgoing arcs excepted (u,v)
        for (int w = graph[u].nextSetBit(0); w >= 0; w = graph[u].nextSetBit(w + 1)) {
            if (w != v) {
                graph[u].set(w, false);
                revGraph[w].set(u, false);
            }
        }
        for (int w = graph[u].nextSetBit(0); w >= 0; w = graph[u].nextSetBit(w + 1)) {
            if (w != v && params.contains(Maintain.TRANSITIVE_CLOSURE)) remIncreTC(u, w);
        }
        if (params.contains(Maintain.TRANSITIVE_REDUCTION)) computeTRfromScratch();
        updateSpecialNodes();
        if (params.contains(Maintain.CONNECTED_COMP)) computeCCfromScratch();
    }

    /**
     * remove all the successors of node u that have an index below to idx (required properties are
     * dynamically updated)
     *
     * @param u     index of a node
     * @param idx   integer value
     */
    public void remAllLowerIdx(int u, int idx) {
        for (int v = graph[u].nextSetBit(0); v >= 0; v = graph[u].nextSetBit(v + 1)) {
            if (v < idx) {
                graph[u].set(v, false);
                revGraph[v].set(u, false);
            }
        }
        for (int v = graph[u].nextSetBit(0); v >= 0; v = graph[u].nextSetBit(v + 1)) {
            if (v < idx && params.contains(Maintain.TRANSITIVE_CLOSURE)) remIncreTC(u, v);
        }
        if (params.contains(Maintain.TRANSITIVE_REDUCTION)) computeTRfromScratch();
        updateSpecialNodes();
        if (params.contains(Maintain.CONNECTED_COMP)) computeCCfromScratch();
    }

    /**
     * remove all the successors of node u that have an index higher than idx (required properties are
     * dynamically updated)
     *
     * @param u     index of a node
     * @param idx   integer value
     */
    public void remAllGreaterIdx(int u, int idx) {
        for (int v = graph[u].nextSetBit(0); v >= 0; v = graph[u].nextSetBit(v + 1)) {
            if (v > idx) {
                graph[u].set(v, false);
                revGraph[v].set(u, false);
            }
        }
        for (int v = graph[u].nextSetBit(0); v >= 0; v = graph[u].nextSetBit(v + 1)) {
            if (v > idx && params.contains(Maintain.TRANSITIVE_CLOSURE)) remIncreTC(u, v);
        }
        if (params.contains(Maintain.TRANSITIVE_REDUCTION)) computeTRfromScratch();
        updateSpecialNodes();
        if (params.contains(Maintain.CONNECTED_COMP)) computeCCfromScratch();
    }

    /**
     * remove all the successors of node u that have an index below to inf and higher
     * than sup (required properties are dynamically updated)
     *
     * @param u     index of a node
     * @param inf   integer value
     * @param sup   integer value
     */
    public void remAllIdx(int u, int inf, int sup) {
        for (int v = graph[u].nextSetBit(0); v >= 0; v = graph[u].nextSetBit(v + 1)) {
            if (v < inf || v > sup) {
                graph[u].set(v, false);
                revGraph[v].set(u, false);
            }
        }
        for (int v = graph[u].nextSetBit(0); v >= 0; v = graph[u].nextSetBit(v + 1)) {
            if (v < inf || v > sup) {
                if (params.contains(Maintain.TRANSITIVE_CLOSURE)) remIncreTC(u, v);
            }
        }
        if (params.contains(Maintain.TRANSITIVE_REDUCTION)) computeTRfromScratch();
        updateSpecialNodes();
        if (params.contains(Maintain.CONNECTED_COMP)) computeCCfromScratch();
    }

    /**
     * all the arc (u,v), such that v belongs to the set depicted by the iterator, are removed
     *
     * @param u     index of a node
     * @param deltaDomain   an iterator over the removed indices
     */
    public void remAllNodes(int u, IntIterator deltaDomain) {
        while (deltaDomain.hasNext()) {
            int v = deltaDomain.next();
            graph[u].set(v, false);
            revGraph[v].set(u, false);
        }
        while (deltaDomain.hasNext()) {
            int v = deltaDomain.next();
            if (params.contains(Maintain.TRANSITIVE_CLOSURE)) remIncreTC(u, v);
        }
        if (params.contains(Maintain.TRANSITIVE_REDUCTION)) computeTRfromScratch();
        updateSpecialNodes();
        if (params.contains(Maintain.CONNECTED_COMP)) computeCCfromScratch();
    }

    private void updateSpecialNodes(int u, int v) {
        if (graph[u].cardinality() == 0) sinkNodes.set(u, true);
        else sinkNodes.set(u, false);
        if (revGraph[v].cardinality() == 0) srcNodes.set(v, true);
        else srcNodes.set(v, false);
    }

    private void updateSpecialNodes() {
        for (int i = 0; i < nbNodes; i++) {
            if (graph[i].cardinality() == 0) sinkNodes.set(i, true);
            else sinkNodes.set(i, false);
            if (revGraph[i].cardinality() == 0) srcNodes.set(i, true);
            else srcNodes.set(i, false);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////// Accesseurs pour la structure ///////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////

    public int getGraphSize() {
        return nbNodes;
    }

    public StoredBitSet getSuccessors(int i) {
        return graph[i];
    }

    public StoredBitSet getPredecessors(int i) {
        return revGraph[i];
    }

    public StoredBitSet getDescendants(int i) {
        return tcGraph[i];
    }

    public StoredBitSet getAncestors(int i) {
        return revTcGraph[i];
    }

    public StoredBitSet[] getGraph() {
        return graph;
    }

    public void setGraph(StoredBitSet[] newGraph) {
        razGraph();
        for (int i = 0; i < nbNodes; i++) {
            for (int j = newGraph[i].nextSetBit(0); j >= 0; j = newGraph[i].nextSetBit(j + 1)) {
                graph[i].set(j, true);
            }
        }
    }

    public void razGraph() {
        for (int i = 0; i < nbNodes; i++) {
            for (int j = graph[i].nextSetBit(0); j >= 0; j = graph[i].nextSetBit(j + 1)) {
                graph[i].set(j, false);
            }
        }
    }

    public StoredBitSet[] getRevGraph() {
        return revGraph;
    }

    public StoredBitSet[] getTcGraph() {
        return tcGraph;
    }

    public StoredBitSet[] getRevTcGraph() {
        return revTcGraph;
    }

    public StoredBitSet[] getTrGraph() {
        return trGraph;
    }

    public StoredBitSet[] getRevTrGraph() {
        return revTrGraph;
    }

    public StoredBitSet getSrcNodes() {
        return srcNodes;
    }

    public StoredBitSet getSinkNodes() {
        return sinkNodes;
    }

    public Vector<StoredBitSet> getSetCC() {
        return setCC;
    }

    public StoredBitSet[] getVertFromNumCC() {
        return vertFromNumCC;
    }

    public StoredBitSet[] getNumFromVertCC() {
        return numFromVertCC;
    }

    public IStateInt getNbCC() {
        return nbCC;
    }

    public String showDesc(int i, String type) {
        String s = ("D_" + type + "[" + i + "] = ");
        for (int j = tcGraph[i].nextSetBit(0); j >= 0; j = tcGraph[i].nextSetBit(j + 1)) {
            s += j + " ";
        }
        return s;
    }

    public void showAllDesc(String type) {
        for (int i = 0; i < nbNodes; i++) {
            System.out.print(type + "" + i + ":=");
            for (int j = tcGraph[i].nextSetBit(0); j >= 0; j = tcGraph[i].nextSetBit(j + 1)) {
                System.out.print(" " + j);
            }
            System.out.println("");
        }
    }

    public void showGraph(String type) {
        for (int i = 0; i < nbNodes; i++) {
            System.out.print(type + "" + i + ":=");
            for (int j = graph[i].nextSetBit(0); j >= 0; j = graph[i].nextSetBit(j + 1)) {
                System.out.print(" " + j);
            }
            System.out.println("");
        }
    }

    public void affiche() {
        System.out.println("************ Graph **************");
        for (int i = 0; i < nbNodes; i++) {
            System.out.print("graph[" + i + "] = ");
            for (int j = graph[i].nextSetBit(0); j >= 0; j = graph[i].nextSetBit(j + 1)) System.out.print(j + " ");
            System.out.println("");
        }
        System.out.println("*********************************");
        if (params.contains(Maintain.TRANSITIVE_CLOSURE)) {
            System.out.println("************ TC Graph **************");
            for (int i = 0; i < nbNodes; i++) {
                System.out.print("TCgraph[" + i + "] = ");
                for (int j = tcGraph[i].nextSetBit(0); j >= 0; j = tcGraph[i].nextSetBit(j + 1))
                    System.out.print(j + " ");
                System.out.println("");
            }
            System.out.println("*********************************");
        }
        if (params.contains(Maintain.TRANSITIVE_REDUCTION)) {
            System.out.println("************ TR Graph **************");
            for (int i = 0; i < nbNodes; i++) {
                System.out.print("TRgraph[" + i + "] = ");
                for (int j = trGraph[i].nextSetBit(0); j >= 0; j = trGraph[i].nextSetBit(j + 1))
                    System.out.print(j + " ");
                System.out.println("");
            }
            System.out.println("*********************************");
        }
    }
}
