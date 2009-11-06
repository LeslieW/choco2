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
import choco.cp.model.managers.SetConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.set.IsNotIncluded;
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
 *    \               IsNotIncluded constraint
 *    \
 *    |
 */
/**
 * A manager to build new IsNotIncluded constraint
 */
public class IsNotIncludedManager extends SetConstraintManager {
  public SConstraint makeConstraint(Solver solver, SetVariable[] vars, Object parameters, HashSet<String> options) {
    if (solver instanceof CPSolver) {
      return new IsNotIncluded(solver.getVar(vars[0]), solver.getVar(vars[1]));
    }
    if (Choco.DEBUG) {
      LOGGER.severe("Could not found an implementation of isnotincluded !");
    }
    return null;
  }
}
