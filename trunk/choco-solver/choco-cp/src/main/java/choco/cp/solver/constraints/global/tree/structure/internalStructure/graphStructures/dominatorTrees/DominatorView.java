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
package choco.cp.solver.constraints.global.tree.structure.internalStructure.graphStructures.dominatorTrees;

import choco.cp.solver.constraints.global.tree.structure.internalStructure.graphStructures.algorithms.Dominators;
import choco.cp.solver.constraints.global.tree.structure.internalStructure.graphStructures.graphViews.PrecsGraphView;
import choco.cp.solver.constraints.global.tree.structure.internalStructure.graphStructures.graphViews.VarGraphView;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.Solver;

import java.util.BitSet;
import java.util.logging.Logger;

public class DominatorView {

     protected final static Logger LOGGER = ChocoLogging.getEngineLogger();

    // le solveur choco
    protected Solver solver;

    // le nombre de sommets dans le graphe
    protected int nbNodes;

    // le graphe sur lequel on cherche les dominants
    protected VarGraphView graph;

    // un ordre partiel entre les sommets du graphe
    protected PrecsGraphView precs;

    // les sommets dominants
    protected Dominators dom;
    //protected StoredJavaBitSet[][] dominators;
    protected BitSet[][] dominators;

    protected boolean update;

    public DominatorView(Solver solver, VarGraphView graph, PrecsGraphView precs) {
        this.solver = solver;
        this.graph = graph;
        this.precs = precs;
        this.nbNodes = graph.getNbNodes();
        //this.dominators = new StoredJavaBitSet[nbNodes][nbNodes];
        this.dominators = new BitSet[nbNodes][nbNodes];
        for (int i = 0; i < nbNodes; i++) {
            for (int j = 0; j < nbNodes; j++) {
                //dominators[i][j] = new StoredJavaBitSet(pb.getEnvironment(), nbNodes);
                dominators[i][j] = new BitSet(nbNodes);
                dominators[i][j].set(0, nbNodes, true);
            }
        }
        this.dom = new Dominators(graph, precs);
        updateDominators();
    }

    // mise � jour des sommets dominants
    public void updateDominators() {
        //BitSet[][] newDoms = dom.computeDominators();
        dominators = dom.computeDominators();
        //updateDoms(newDoms);
    }

    private void updateDoms(BitSet[][] newDoms) {
        update = false;
        for (int i = 0; i < nbNodes; i++) {
            for (int j = 0; j < nbNodes; j++) {
                for (int k = newDoms[i][j].nextSetBit(0); k >= 0; k = newDoms[i][j].nextSetBit(k + 1)) {
                    if (!dominators[i][j].get(k)) {
                        dominators[i][j].set(k, true);
                        update = true;
                    }
                }
                for (int k = dominators[i][j].nextSetBit(0); k >= 0; k = dominators[i][j].nextSetBit(k + 1)) {
                    if (!newDoms[i][j].get(k)) {
                        dominators[i][j].set(k, false);
                        update = true;
                    }
                }//*/
            }
            //showDoms(i,nbNodes-1);
        }
    }

    /*public StoredJavaBitSet[][] getDominators() {
        return dominators;
    }*/
    public BitSet[][] getDominators() {
        return dominators;
    }

    public boolean isUpdate() {
        return update;
    }

    public void showDoms(int i) {
        for (int j = 0; j < nbNodes; j++) {
            if (j != i) showDoms(i, j);
        }
    }

    public void showDoms(int i, int j) {
        LOGGER.info("dom[" + i + "][" + j + "] = "+dominators[i][j].toString());
    }
}
