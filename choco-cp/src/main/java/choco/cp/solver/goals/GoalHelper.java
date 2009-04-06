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

import choco.kernel.solver.goals.Goal;
import choco.kernel.solver.goals.choice.RemoveVal;
import choco.kernel.solver.goals.choice.SetVal;
import choco.kernel.solver.goals.solver.ChoicePoint;
import choco.kernel.solver.variables.integer.IntDomainVar;


/*
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: 19 mai 2007
 * Since : Choco 2.0.0
 *
 */
public class GoalHelper {
  public static Goal or(Goal... goal) {
    if (goal.length == 0) return null;
    if (goal.length == 1) return goal[0];
    return new ChoicePoint(goal);
  }

  public static Goal and(Goal... goal) {
    if (goal.length == 0) return null;
    if (goal.length == 1) return goal[0];
    return new Sequence(goal);
  }

  public static Goal remVal(IntDomainVar var, int val) {
    return new RemoveVal(var, val);
  }

  public static Goal setVal(IntDomainVar var, int val) {
    return new SetVal(var, val);
  }
}
