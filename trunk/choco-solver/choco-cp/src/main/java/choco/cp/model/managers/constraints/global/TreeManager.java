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
package choco.cp.model.managers.constraints.global;

import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.tree.TreeSConstraint;
import choco.cp.solver.constraints.global.tree.structure.inputStructure.Node;
import choco.cp.solver.constraints.global.tree.structure.inputStructure.TreeParameters;
import choco.kernel.model.ModelException;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.tree.TreeNodeObject;
import choco.kernel.model.variables.tree.TreeParametersObject;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;

import java.util.Set;

/**
 * User:    charles
 * Date:    26 août 2008
 */
public class TreeManager extends IntConstraintManager {

    /**
     * Build a constraint for the given solver and "model variables"
     *
     * @param solver
     * @param variables
     * @param parameters : a "hook" to attach any kind of parameters to constraints
     * @param options
     * @return
     */
    public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables, Object parameters, Set<String> options) {
        if(solver instanceof CPSolver){

            if(parameters instanceof TreeParametersObject){
                TreeParametersObject tpo = (TreeParametersObject)parameters;
                int nbNodes = tpo.getNbNodes();
                TreeNodeObject[] tnodes = tpo.getNodes();
                Node[] nodes = new Node[tnodes.length];
                TreeParameters params = null;
                for(int i =0; i < tnodes.length; i++){
                    TreeNodeObject tn = tnodes[i];
                    nodes[i] = new Node(solver, nbNodes, tn.getIdx(), solver.getVar(tn.getSuccessors()),
                            solver.getVar(tn.getInDegree()), solver.getVar(tn.getTimeWindow()), tpo.getGraphs());
                }

                params  = new TreeParameters(solver, nbNodes, solver.getVar(tpo.getNTree()),
                        solver.getVar(tpo.getNproper()), solver.getVar(tpo.getObjective()), nodes, tpo.getTravel());
                return new TreeSConstraint(params.getAllVars(),params);
            }
        }
        throw new ModelException("Could not found a constraint manager in " + this.getClass() + " !");
    }
}
