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
package choco.cp.solver.constraints.global.tree.deduction;

import choco.cp.solver.constraints.global.tree.structure.inputStructure.Node;
import choco.cp.solver.constraints.global.tree.structure.inputStructure.TreeParameters;
import choco.cp.solver.constraints.global.tree.structure.internalStructure.StructuresAdvisor;
import choco.cp.solver.constraints.global.tree.structure.internalStructure.graphStructures.dominatorTrees.DominatorView;
import choco.cp.solver.constraints.global.tree.structure.internalStructure.graphStructures.graphViews.PrecsGraphView;
import choco.cp.solver.constraints.global.tree.structure.internalStructure.graphStructures.graphViews.StoredBitSetGraph;
import choco.cp.solver.constraints.global.tree.structure.internalStructure.graphStructures.graphViews.VarGraphView;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.Solver;

import java.util.logging.Logger;

public abstract class AbstractDeduction {

    protected final static Logger LOGGER  = ChocoLogging.getSolverLogger();

    protected boolean affiche = true;

    // the number nodes in the graph
    protected int nbVertices;

    protected Solver solver;
    protected TreeParameters tree;
    protected StructuresAdvisor struct;
    protected boolean update;
    protected boolean compatible;

    protected Node[] nodes;
    protected VarGraphView inputGraph;
    protected PrecsGraphView precs;
    protected DominatorView doms;
    protected StoredBitSetGraph incomp;
    protected StoredBitSetGraph condPrecs;

    
    /**
     *
     * @param params    a set of parameters describing each part of the global tree constraint
     * 
     */
    protected AbstractDeduction(Object[] params) {
        this.solver = (Solver) params[0];
        this.tree = (TreeParameters) params[1];
        this.struct = (StructuresAdvisor) params[2];
        this.update = (Boolean) params[3];
        this.compatible = (Boolean) params[4];
        this.affiche = (Boolean) params[5];

        this.nbVertices = tree.getNbNodes();
        this.nodes = tree.getNodes();
        this.inputGraph = struct.getInputGraph();
        this.precs = struct.getPrecs();
        this.doms = struct.getDoms();
        this.incomp = struct.getIncomp();
        this.condPrecs = struct.getCondPrecs();
    }

    public boolean isCompatible() {
        return compatible;
    }

    public boolean isUpdate() {
        return update;
    }
}
