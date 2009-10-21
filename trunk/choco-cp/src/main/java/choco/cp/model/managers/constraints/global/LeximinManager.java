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

import choco.Choco;
import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.LeximinSConstraint;
import choco.cp.solver.constraints.global.SemiLeximinSConstraint;
import choco.kernel.model.variables.integer.IntegerVariable;
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
 *    \                 Leximin constraint
 *    \
 *    |
 */
/**
 * A manager to build new Leximin constraint
 */
public class LeximinManager extends IntConstraintManager {
  public SConstraint makeConstraint(Solver solver, IntegerVariable[] vars, Object parameters, HashSet<String> options) {
    if (solver instanceof CPSolver) {
      if (parameters != null) {
        return new SemiLeximinSConstraint((int[]) parameters, solver.getVar(vars));
      } else {
        return new LeximinSConstraint(solver.getVar(vars));
      }
    }
    if (Choco.DEBUG) {
      LOGGER.severe("Could not found an implementation in " + this.getClass() + " !");
    }
    return null;
  }
}
