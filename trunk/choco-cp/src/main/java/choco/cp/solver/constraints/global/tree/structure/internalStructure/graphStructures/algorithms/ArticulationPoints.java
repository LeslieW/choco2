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


import choco.cp.solver.constraints.global.tree.structure.internalStructure.graphStructures.graphViews.StoredBitSetGraph;
import choco.kernel.memory.IStateBitSet;
import choco.kernel.memory.trailing.StoredBitSet;

import java.util.BitSet;

public class ArticulationPoints {

    protected BitSet[] undirected;
    protected int size;
    protected int[] color;
    protected int time;
    protected int[] predecessor;
    protected int[] prefix;
    protected int[] postfix;
    protected int[] lowpt;
    protected int root;
    protected BitSet res;
    protected IStateBitSet sapNodes;
    protected int nbVertices;

    public ArticulationPoints(StoredBitSetGraph graph, IStateBitSet sapNodes) {
        this.sapNodes = sapNodes;
        this.nbVertices = graph.getGraphSize();
        // transformation de cgaPoss en graphe non-oriente, on oublie l'orientation
        undirected = new BitSet[nbVertices];
        for (int i = 0; i < undirected.length; i++) undirected[i] = new BitSet(nbVertices);
        for (int v = 0; v < nbVertices; v++) {
            StoredBitSet dom = graph.getSuccessors(v);
            for (int j = dom.nextSetBit(0); j >= 0; j = dom.nextSetBit(j + 1)) {
                if (v != j) {
                    undirected[v].set(j, true);
                    undirected[j].set(v, true);
                }
            }
        }
        root = -1;
        color = new int[undirected.length];
        predecessor = new int[undirected.length];
        prefix = new int[undirected.length];
        postfix = new int[undirected.length];
        lowpt = new int[undirected.length];
        for (int i = 0; i < undirected.length; i++) {
            color[i] = 0;
            predecessor[i] = -1;
            prefix[i] = -1;
            postfix[i] = -1;
            lowpt[i] = undirected.length;
        }
        time = 0;
        res = new BitSet(undirected.length);
    }

    public BitSet dfs() {
        for (int k = 0; k < undirected.length; k++) {
            if (color[k] == 0) {
                root = k;
                if (!sapNodes.get(root)) dfsVisit(root);
            }
        }
        int nb = 0;
        for (int k = 0; k < undirected.length; k++) {
            if (predecessor[k] == root && root != -1) nb++;
        }
        if (nb > 1) {
            if (!sapNodes.get(root)) res.set(root, true);
        }
        return res;
    }

    public void dfsVisit(int u) {
        color[u] = 1;
        time++;
        prefix[u] = time;
        if (lowpt[u] > time) lowpt[u] = time;
        BitSet adj = undirected[u];
        for (int k = adj.nextSetBit(0); k >= 0; k = adj.nextSetBit(k + 1)) {
            if (color[k] == 0) {
                predecessor[k] = u;
                dfsVisit(k);
                if (lowpt[u] > lowpt[k]) lowpt[u] = lowpt[k];
                if (lowpt[k] >= prefix[u] && u != root && !sapNodes.get(u)) {
                    res.set(u, true);
                }
            } else {
                if (k != predecessor[u]) {
                    if (prefix[k] < prefix[u]) {
                        if (lowpt[u] > prefix[k]) lowpt[u] = prefix[k];
                    }
                }
            }
        }
        color[u] = 2;
        time++;
        postfix[u] = time;
    }
}