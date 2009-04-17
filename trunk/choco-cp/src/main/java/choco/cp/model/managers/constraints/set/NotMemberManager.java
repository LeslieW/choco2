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

import choco.Choco;
import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.set.NotMemberX;
import choco.cp.solver.constraints.set.NotMemberXY;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;

import java.util.HashSet;

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
public class NotMemberManager extends IntConstraintManager {
  public SConstraint makeConstraint(Solver solver, Variable[] vars, Object parameters, HashSet<String> options) {
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
    if (Choco.DEBUG) {
      LOGGER.severe("Could not found an implementation in " + this.getClass() + " !");
    }
    return null;
  }
}
