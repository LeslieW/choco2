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
package choco.cp.solver.constraints.global.tree.filtering;


import choco.cp.solver.constraints.global.tree.structure.inputStructure.Node;
import choco.cp.solver.constraints.global.tree.structure.inputStructure.TreeParameters;
import choco.cp.solver.constraints.global.tree.structure.internalStructure.StructuresAdvisor;
import choco.cp.solver.constraints.global.tree.structure.internalStructure.costStrutures.CostStructure;
import choco.cp.solver.constraints.global.tree.structure.internalStructure.graphStructures.dominatorTrees.DominatorView;
import choco.cp.solver.constraints.global.tree.structure.internalStructure.graphStructures.graphViews.PrecsGraphView;
import choco.cp.solver.constraints.global.tree.structure.internalStructure.graphStructures.graphViews.StoredBitSetGraph;
import choco.cp.solver.constraints.global.tree.structure.internalStructure.graphStructures.graphViews.VarGraphView;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;

import java.io.IOException;
import java.util.logging.Logger;


public abstract class AbstractPropagator {

    protected final static Logger LOGGER = ChocoLogging.getSolverLogger();

    /**
     * Choco solver embedding the tree constraint
     */
    protected Solver solver;

    /**
     * attributes
     */
    protected TreeParameters tree;
    protected Node[] nodes;

    /**
     * a table that manage the indices of the variables involved in the tree constraint
     */
    protected int[] indices;

    /**
     * structure advisor
     */
    protected StructuresAdvisor struct;

    /**
     * cost structure advisor
     */
    protected CostStructure costStruct;

    /**
     * structure that manage removals
     */
    protected RemovalsAdvisor propagateStruct;

    /**
     * boolean for debug and show a trace of the execution
     */
    protected boolean affiche;

    /**
     * total number of nodes involved in the graph
     */
    protected int nbVertices;

    /**
     * data structure related to the graph representation of the input graph
     */
    protected VarGraphView inputGraph;

    /**
     * data structure related to the graph representation of the partial order
     */
    protected PrecsGraphView precs;

    /**
     * data structure related to the graph representation of the dominator nodes of this graph
     */
    protected DominatorView doms;

    /**
     * data structure related to the graph representation of the incomparability constraint
     */
    protected StoredBitSetGraph incomp;

    /**
     * data structure related to the graph representation of the conditional partial order
     */
    protected StoredBitSetGraph condPrecs;

    /**
     * Constructor: abstract propagator structure
     *
     * @param params    a set of parameters describing each part of the global tree constraint
     */
    protected AbstractPropagator(Object[] params) {
        this.solver = (Solver) params[0];
        this.tree = (TreeParameters) params[1];
        this.indices = (int[]) params[2];
        this.struct = (StructuresAdvisor) params[3];
        this.costStruct = (CostStructure) params[4];
        this.propagateStruct = (RemovalsAdvisor) params[5];
        this.affiche = (Boolean) params[6];
        this.nodes = tree.getNodes();
        this.nbVertices = nodes.length;
        this.inputGraph = struct.getInputGraph();
        this.precs = struct.getPrecs();
        this.doms = struct.getDoms();
        this.incomp = struct.getIncomp();
        this.condPrecs = struct.getCondPrecs();
    }

    public abstract String getTypePropag();

    /**
     * a generic method that manage the filtering methods
     *
     * @return  <code> false </code> iff an inconsistency is detected
     * @throws choco.kernel.solver.ContradictionException
     * @throws IOException
     */
    public boolean applyConstraint() throws ContradictionException, IOException {
        if (!feasibility()) {
            if (affiche) LOGGER.info("==> Fail() on feasibility() test");
            return false;
        } else {
            filter();
            return true;
        }
    }

    /**
     * check the consistency of the filtering rules of a given propagator
     *
     * @return <code> false </code> iff the propagator detect an inconsistency
     * @throws ContradictionException
     */
    public abstract boolean feasibility() throws ContradictionException;

    /**
     * record the inconsistant values with the variables of a given propagator
     * 
     * @throws ContradictionException
     */
    public abstract void filter() throws ContradictionException;

}
