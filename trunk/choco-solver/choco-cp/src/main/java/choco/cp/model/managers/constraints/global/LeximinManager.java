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
import choco.cp.solver.constraints.global.LeximinSConstraint;
import choco.cp.solver.constraints.global.SemiLeximinSConstraint;
import choco.kernel.model.ModelException;
import choco.kernel.model.variables.integer.IntegerVariable;
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
 *    \                 Leximin constraint
 *    \
 *    |
 */
/**
 * A manager to build new Leximin constraint
 */
public final class LeximinManager extends IntConstraintManager {
  public SConstraint makeConstraint(Solver solver, IntegerVariable[] vars, Object parameters, Set<String> options) {
    if (solver instanceof CPSolver) {
      if (parameters != null) {
        return new SemiLeximinSConstraint((int[]) parameters, solver.getVar(vars), solver.getEnvironment());
      } else {
        return new LeximinSConstraint(solver.getVar(vars), solver.getEnvironment());
      }
    }
    throw new ModelException("Could not found a constraint manager in " + this.getClass() + " !");
  }
}
