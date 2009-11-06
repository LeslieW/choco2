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

import choco.cp.solver.CPSolver;
import choco.cp.solver.search.GoalSearchLoop;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.goals.Goal;
import choco.kernel.solver.goals.GoalType;


/*
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: 19 mai 2007
 * Since : Choco 2.0.0
 *
 */
public class Sequence implements Goal {
  protected Goal[] sequence;

  public Sequence(Goal[] goals) {
    this.sequence = goals;
  }

  public String pretty() {
    StringBuilder sb = new StringBuilder();
    sb.append("and(");
    for (int i = 0; i < sequence.length; i++) {
      if (i>0) sb.append(", ");
      Goal goal = sequence[i];
      sb.append(goal.pretty());
    }
    sb.append(")");
    return sb.toString();
  }

  public Goal execute(Solver s) throws ContradictionException {
    if(CPSolver.GOAL){
        GoalSearchSolver gsl = (GoalSearchSolver) s.getSearchStrategy();
        for (int i = sequence.length - 1; i >= 0; i--) {
          Goal goal = sequence[i];
          gsl.pushGoal(goal);
        }
    }else{

      GoalSearchLoop gsl = (GoalSearchLoop) s.getSearchStrategy().searchLoop;
        for (int i = sequence.length - 1; i >= 0; i--) {
            Goal goal = sequence[i];
            gsl.pushGoal(goal);
        }
    }
      return null;
  }

    @Override
    public GoalType getType() {
        return GoalType.SEQ;
    }
}
