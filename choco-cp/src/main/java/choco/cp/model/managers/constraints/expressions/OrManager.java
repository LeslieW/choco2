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
package choco.cp.model.managers.constraints.expressions;

import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.constraints.reified.leaves.bool.OrNode;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.MetaConstraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.reified.INode;

import java.util.HashSet;

/**
 * ***********************************************
 * _       _                            *
 * |  °(..)  |                           *
 * |_  J||L _|        ChocoSolver.net    *
 * *
 * Choco is a java library for constraint     *
 * satisfaction problems (CSP), constraint    *
 * programming (CP) and explanation-based     *
 * constraint solving (e-CP). It is built     *
 * on a event-based propagation mechanism     *
 * with backtrackable structures.             *
 * *
 * Choco is an open-source software,          *
 * distributed under a BSD licence            *
 * and hosted by sourceforge.net              *
 * *
 * + website : http://choco.emn.fr            *
 * + support : choco@emn.fr                   *
 * *
 * Copyright (C) F. Laburthe,                 *
 * N. Jussien    1999-2008      *
 * *************************************************
 * User:    charles
 * Date:    22 août 2008
 */
public class OrManager extends IntConstraintManager {

    /**
     * Build a constraint for the given solver and "model variables"
     *
     * @param solver
     * @param variables
     * @param parameters : a "hook" to attach any kind of parameters to constraints
     * @param options
     * @return
     */
    public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables, Object parameters, HashSet<String> options) {
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
    public INode makeNode(Solver solver, Constraint[] cstrs, IntegerExpressionVariable[] vars) {
        MetaConstraint mc = (MetaConstraint)cstrs[0];
        INode[] nt = new INode[mc.getConstraints().length];
        for (int i = 0; i < mc.getConstraints().length; i++) {
            Constraint c = mc.getConstraints()[i];
            IntegerExpressionVariable[] ev = new IntegerExpressionVariable[c.getNbVars()];
            for(int j = 0; j < c.getNbVars(); j++){
                ev[j]  = (IntegerExpressionVariable)c.getVariables()[j];
            }
            nt[i] = c.getEm().makeNode(solver, new Constraint[]{c}, ev);
        }
        return new OrNode(nt);
    }
}
