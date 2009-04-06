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
package choco.cp.solver.constraints.global.tree.structure.internalStructure.graphStructures.algorithms;

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


import choco.kernel.memory.trailing.StoredBitSet;
import choco.kernel.solver.Solver;

import java.util.BitSet;
import java.util.Vector;

public class ConnectedComponents {

    protected boolean affiche;

    protected Solver solver;

    protected StoredBitSet[] graph;

    protected int nbNodes;
    protected BitSet[] undirected;
    protected Vector<StoredBitSet> cc;
    protected int nbCC;

    protected int[] color;
    protected BitSet reached;

    public ConnectedComponents(Solver solver, int nbNodes, StoredBitSet[] graph,
                               Vector<StoredBitSet> cc) {
        this.solver = solver;
        this.nbNodes = nbNodes;
        this.graph = graph;
        this.cc = cc;
        this.color = new int[nbNodes];
        undirected = new BitSet[nbNodes];
    }

    private void update() {
        this.nbCC = 0;
        this.color = new int[nbNodes];
        this.reached = new BitSet(nbNodes);
        getUndirectedGraph();
        if (affiche) showGraph();
        for (int i = 0; i < nbNodes; i++) cc.elementAt(i).clear();
    }

    private void showGraph() {
        for (int i = 0; i < nbNodes; i++) {
            StoredBitSet contain = graph[i];
            System.out.print("sure[" + i + "] = "+contain.toString());
            System.out.println("");
        }
        for (int i = 0; i < nbNodes; i++) {
            BitSet contain = undirected[i];
            System.out.print("undirected[" + i + "] = "+contain.toString());
            System.out.println("");
        }
        System.out.println("**************************");
    }

    private void getUndirectedGraph() {
        for (int i = 0; i < nbNodes; i++) undirected[i] = new BitSet(nbNodes);
        for (int v = 0; v < nbNodes; v++) {
            for (int j = graph[v].nextSetBit(0); j >= 0; j = graph[v].nextSetBit(j + 1)) {
                if (v != j) {
                    undirected[v].set(j, true);
                    undirected[j].set(v, true);
                }
            }
        }
    }

    public void getConnectedComponents(boolean b) {
        affiche = b;
        update();
        /*if (affiche) {
            showGraph();
            System.out.println("----------------");
        }*/
        for (int i = 0; i < nbNodes; i++) color[i] = 0;
        int u = existsUnVisited();
        while (u != -1) {
            reached.set(u,true);
            if (affiche) System.out.print("cc[" + u + "] = ");
            dfsVisit(u);
            if (affiche) System.out.println("");
            StoredBitSet toModif = cc.remove(u);
            toModif.clear();
            for (int j = reached.nextSetBit(0); j >= 0; j = reached.nextSetBit(j + 1)) toModif.set(j,true);
            cc.insertElementAt(toModif, u);
            reached.clear();
            //for (int j = reached.nextSetBit(0); j >= 0; j = reached.nextSetBit(j + 1)) reached.set(j, false);
            u = existsUnVisited();
            nbCC++;
        }
        if (affiche) System.out.println("----------------");
    }

    private void convertToStored(Vector<BitSet> vb) {
        for (int i = 0; i < nbNodes; i++) {
            BitSet b = vb.elementAt(i);
            StoredBitSet c = cc.elementAt(i);
            c.clear();
            for (int j = b.nextSetBit(0); j >= 0; j = b.nextSetBit(j + 1)) c.set(j,true);
        }
    }

    private void dfsVisit(int u) {
        if (affiche) System.out.print(u + " ");
        color[u] = 1;
        reached.set(u, true);
        BitSet adj = undirected[u];
        for (int v = adj.nextSetBit(0); v >= 0; v = adj.nextSetBit(v + 1)) {
            if (color[v] == 0) dfsVisit(v);
        }
    }

    // choix d'un sommet parmi les possibles
    protected int existsUnVisited() {
        for (int i = 0; i < nbNodes; i++) {
            if (color[i] == 0) return i;
        }
        return -1;
    }

    public int getNbCC() {
        return nbCC;
    }
}
