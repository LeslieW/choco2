package i_want_to_use_this_old_version_of_choco.goals.solver;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.goals.Goal;

/**
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: 19 mai 2007
 * Time: 10:13:50
 * To change this template use File | Settings | File Templates.
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

  public Goal execute(AbstractProblem problem) throws ContradictionException {
    GoalSearchSolver gss = (GoalSearchSolver) problem.getSolver().getSearchSolver();
    for (int i = sequence.length - 1; i >= 0; i--) {
      Goal goal = sequence[i];
      gss.pushGoal(goal);
    }
    return null;
  }
}
