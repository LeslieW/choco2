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
package choco.kernel.solver.goals.solver;

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
public class ChoicePoint implements Goal {
  protected Goal[] choices;

  public ChoicePoint(Goal[] goals) {
    choices = goals;
  }

  public String pretty() {
    StringBuilder sb = new StringBuilder();
    sb.append("or(");
    for (int i = 0; i < choices.length; i++) {
      if (i>0) sb.append(", ");
      Goal goal = choices[i];
      sb.append(goal.pretty());
    }
    sb.append(")");
    return sb.toString();
  }

  public Goal execute(Solver solver) throws ContradictionException {
    LOGGER.severe("Should not be called in ChoicePoint !!");
    return null;
  }

    @Override
    public GoalType getType() {
        return GoalType.CHOICE;
    }

    public Goal getChoice(int choiceIndex) {
    return choices[choiceIndex];
  }

  public int getNbChoices() {
    return choices.length;
  }
}
