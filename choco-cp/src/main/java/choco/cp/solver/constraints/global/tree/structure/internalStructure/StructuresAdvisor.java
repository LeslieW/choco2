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
package choco.cp.solver.constraints.global.tree.structure.internalStructure;

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


import choco.cp.solver.constraints.global.tree.structure.inputStructure.Node;
import choco.cp.solver.constraints.global.tree.structure.inputStructure.TreeParameters;
import choco.cp.solver.constraints.global.tree.structure.internalStructure.costStrutures.CostStructure;
import choco.cp.solver.constraints.global.tree.structure.internalStructure.degreeStructure.DegreeStructure;
import choco.cp.solver.constraints.global.tree.structure.internalStructure.graphStructures.dominatorTrees.DominatorView;
import choco.cp.solver.constraints.global.tree.structure.internalStructure.graphStructures.graphViews.PrecsGraphView;
import choco.cp.solver.constraints.global.tree.structure.internalStructure.graphStructures.graphViews.StoredBitSetGraph;
import choco.cp.solver.constraints.global.tree.structure.internalStructure.graphStructures.graphViews.VarGraphView;
import choco.kernel.memory.trailing.StoredBitSet;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.ArrayList;
import java.util.List;


/**
 * An advisor that manages the several data structures involved in the constraint.
 *
 */
public class StructuresAdvisor {

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
     * total number of nodes involved in the graph
     */
    protected int nbNodes;

    /**
     * data structure related to the degree constraints related to each node
     */
    protected DegreeStructure degree;
    protected boolean updateDegree;

    /**
     * data structure related to the graph representation of the input graph
     */
    protected VarGraphView inputGraph;

    /**
     * data structure related to the graph representation of the dominator nodes of this graph
     */
    protected DominatorView doms;

    /**
     * data structure related to the graph representation of the partial order
     */
    protected PrecsGraphView precs;

    /**
     * data structure related to the graph representation of the conditional partial order
     */
    protected StoredBitSetGraph condPrecs;

    /**
     * data structure related to the graph representation of the incomparability constraint
     */
    protected StoredBitSetGraph incomp;

    /**
     * data structures related to the costs associated with each arcs of the graph
     */
    protected CostStructure costStruct;


    /**
     * constructor: build an advisor for the internal data structures
     *
     * @param solver    the Choco solver who uses the current tree constraint.
     * @param tree  the input data structure available in the <code> structure.inputStructure </code> package.
     */
    public StructuresAdvisor(Solver solver, TreeParameters tree) {
        this.solver = solver;
        this.tree = tree;

        this.nodes = tree.getNodes();
        this.nbNodes = tree.getNbNodes();

        this.inputGraph = createVarGraphView();
        this.precs = createPrecsGraphView();
        this.doms = createDominatorView();
        this.incomp = createIncompGraph();
        this.condPrecs = createCondPrecsGraph();
        this.degree = createDegreeStructure();
        this.costStruct = new CostStructure(this.solver, tree, inputGraph);
    }

    private VarGraphView createVarGraphView() {
        IntDomainVar[] s = new IntDomainVar[nbNodes];
        for (int i = 0; i < nbNodes; i++) {
			s[i] = nodes[i].getSuccessors();
		}
        return new VarGraphView(solver, s);
    }

    private PrecsGraphView createPrecsGraphView() {
        return new PrecsGraphView(solver, nodes);
    }

    private DegreeStructure createDegreeStructure() {
        DegreeStructure deg = new DegreeStructure(this.solver,this.tree,inputGraph);
        this.updateDegree = false;
        return deg;
    }

    private DominatorView createDominatorView() {
        return new DominatorView(solver, inputGraph, precs);
    }

    private StoredBitSetGraph createIncompGraph() {
        List<StoredBitSetGraph.Maintain> params = new ArrayList<StoredBitSetGraph.Maintain>();
        StoredBitSet[] dataIncs = new StoredBitSet[nbNodes];
        for (int i = 0; i < nbNodes; i++) {
			dataIncs[i] = nodes[i].getIncomparableNodes();
		}
        return new StoredBitSetGraph(solver, dataIncs, params, false);
    }

    private StoredBitSetGraph createCondPrecsGraph() {
        List<StoredBitSetGraph.Maintain> params = new ArrayList<StoredBitSetGraph.Maintain>();
        StoredBitSet[] dataConds = new StoredBitSet[nbNodes];
        for (int i = 0; i < nbNodes; i++) {
			dataConds[i] = nodes[i].getCondSuccessors();
		}
        return new StoredBitSetGraph(solver, dataConds, params, false);
    }

    /**
     * the main method that allows to update the degree constraint and the cost structure
     * according to the current state of the graph.
     *
     * @throws choco.kernel.solver.ContradictionException
     */
    public void applyStructure() throws ContradictionException {
        // update the indegree of each node according to the current <code> inputGraph </code>
        updateDegree = false;
        if (degree.needUpdate()) {
            degree.updateDegree();
            updateDegree = true;
        }
        // update the cost structure
        costStruct.updateCostStruct();
    }

    public CostStructure getCostStruct() {
        return costStruct;
    }

    public boolean isUpdateDegree() {
        return updateDegree;
    }

    public DegreeStructure getDegree() {
        return degree;
    }

    public StoredBitSetGraph getIncomp() {
        return incomp;
    }

    public PrecsGraphView getPrecs() {
        return this.precs;
    }

    public DominatorView getDoms() {
        return doms;
    }

    public StoredBitSetGraph getCondPrecs() {
        return condPrecs;
    }

    public VarGraphView getInputGraph() {
        return inputGraph;
    }

}
