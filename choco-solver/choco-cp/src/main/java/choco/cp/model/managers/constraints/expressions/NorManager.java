/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _        _                           *
 *         |   (..)  |                           *
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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.model.managers.constraints.expressions;

import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.integer.bool.BooleanFactory;
import choco.cp.solver.constraints.reified.leaves.bool.NorNode;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.MetaConstraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.reified.INode;

import java.util.Set;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 18 mai 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public class NorManager extends IntConstraintManager{
    /**
     * Build a constraint for the given solver and "model variables"
     *
     * @param solver     solver to build constraint in
     * @param variables  array of variables
     * @param parameters Object defining the paramaters
     * @param options    set of options
     * @return One SConstraint
     */
    @Override
    public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables, Object parameters, Set<String> options) {
        if (solver instanceof CPSolver) {
                if (parameters == null) {
                    BooleanFactory.nor(solver.getVar(variables));
                }
            }
            throw new ModelException("Could not found a constraint manager in " + this.getClass() + " !");
    }

    /**
     * Build arithm node from a IntegerExpressionVariable
     *
     * @param solver
     * @param cstrs  constraints (can be null)
     * @param vars   variables
     * @return
     */
    @Override
    public INode makeNode(final Solver solver, final Constraint[] cstrs, final Variable[] vars) {
        MetaConstraint mc = (MetaConstraint)cstrs[0];
        INode[] nt = new INode[mc.getConstraints().length];
        for (int i = 0; i < mc.getConstraints().length; i++) {
            Constraint c = mc.getConstraints()[i];
            Variable[] ev = new Variable[c.getNbVars()];
            for(int j = 0; j < c.getNbVars(); j++){
                ev[j]  = c.getVariables()[j];
            }
            nt[i] = c.getExpressionManager().makeNode(solver, new Constraint[]{c},ev);
        }
        return new NorNode(nt);
    }
}