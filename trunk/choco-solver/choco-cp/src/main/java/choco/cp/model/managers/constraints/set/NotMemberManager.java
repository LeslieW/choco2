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
package choco.cp.model.managers.constraints.set;

import choco.cp.model.managers.MixedConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.set.NotMemberX;
import choco.cp.solver.constraints.set.NotMemberXY;
import choco.kernel.model.ModelException;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;

import java.util.Set;

/*
 *  ______
 * (__  __)
 *    ||
 *   /__\                  Choco manager
 *    \                    =============
 *    \                      Aug. 2008
 *    \                 NotMember constraint
 *    \
 *    |
 */
/**
 * A manager to build new NotMember constraint
 */
public final class NotMemberManager extends MixedConstraintManager {
  public SConstraint makeConstraint(Solver solver, Variable[] vars, Object parameters, Set<String> options) {
    if (solver instanceof CPSolver) {
        if(vars[0] instanceof SetVariable){
          if (vars.length == 2) {
            return new NotMemberXY(solver.getVar((SetVariable) vars[0]), solver.getVar((IntegerVariable) vars[1]));
          } else {
            return new NotMemberX(solver.getVar((SetVariable) vars[0]), (Integer) parameters);
          }
        }else if(vars[0] instanceof IntegerExpressionVariable){
            
        }
    }
    throw new ModelException("Could not found a constraint manager in " + this.getClass() + " !");
  }
}
