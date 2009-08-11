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
package choco.cp.solver.goals;

import choco.kernel.solver.ContradictionException;
import static choco.kernel.solver.ContradictionException.Type.UNKNOWN;
import choco.kernel.solver.Solver;
import choco.kernel.solver.goals.Goal;


/*
 * Created by IntelliJ IDEA.
 * User: GROCHART
 * Date: 11 janv. 2008
 * Since : Choco 2.0.0
 *
 */
public class GlobalFail implements Goal {
  public String pretty()
  {
    return "Fail";
  }

  public Goal execute(Solver solver) throws ContradictionException {
    ((GoalSearchSolver)solver.getSearchStrategy()).setGlobalContradiction();
    solver.getPropagationEngine().raiseContradiction(null, UNKNOWN);
    return null;
    //throw new ContradictionException(solver);
  }
}
