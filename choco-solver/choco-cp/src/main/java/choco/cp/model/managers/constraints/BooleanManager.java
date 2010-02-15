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
package choco.cp.model.managers.constraints;

import java.util.Set;

import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.reified.leaves.bool.FalseNode;
import choco.cp.solver.constraints.reified.leaves.bool.TrueNode;
import choco.kernel.model.constraints.ComponentConstraint;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintManager;
import choco.kernel.model.variables.Variable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.reified.INode;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 11 août 2008
 * Time: 13:00:17
 */
public class BooleanManager extends ConstraintManager<Variable> {

    /**
     * Build a constraint for the given solver and "model variables"
     *
     * @param solver
     * @param variables
     * @param parameters : a "hook" to attach any kind of parameters to constraints
     * @param options
     * @return
     */
    @Override
    public SConstraint makeConstraint(Solver solver, Variable[] variables, Object parameters, Set<String> options) {
        if(parameters instanceof Boolean){
            boolean bool = (Boolean)parameters;
            if(bool){
                return CPSolver.TRUE;
            }else{
                return CPSolver.FALSE;
            }
        }
        return null;
    }

    /**
     * Build a constraint and its opposite for the given solver and "model variables"
     *
     * @param solver
     * @param variables
     * @param parameters
     * @param options
     * @return array of 2 SConstraint object, the constraint and its opposite
     */
    @Override
    public SConstraint[] makeConstraintAndOpposite(Solver solver, Variable[] variables, Object parameters, Set<String> options) {
         if(parameters instanceof Boolean){
            boolean bool = (Boolean)parameters;
            if(bool){
                return new SConstraint[]{CPSolver.TRUE, CPSolver.FALSE};
            }else{
                return new SConstraint[]{CPSolver.FALSE, CPSolver.TRUE};
            }
        }
        return null;
    }

    /**
     * @param options : the set of options on the constraint (Typically the level of consistency)
     * @return a list of domains accepted by the constraint and sorted
     *         by order of preference
     */
    @Override
    public int[] getFavoriteDomains(Set<String> options) {
        return null;
    }

    /**
     * Build arithm node from a IntegerExpressionVariable
     *
     * @param solver
     * @param cstrs  constraints (can be null)
     * @param vars   variables
     * @return
     */
    public INode makeNode(Solver solver, Constraint[] cstrs, Variable[] vars) {
        ComponentConstraint cc = (ComponentConstraint)cstrs[0];
        if(cc.getParameters() instanceof Boolean){
            boolean bool = (Boolean)cc.getParameters();
            if(bool){
                return new TrueNode();
            }else{
                return new FalseNode();
            }
        }
        return null;
    }
}