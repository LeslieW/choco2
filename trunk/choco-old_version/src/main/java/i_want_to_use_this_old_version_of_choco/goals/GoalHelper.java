package i_want_to_use_this_old_version_of_choco.goals;

import i_want_to_use_this_old_version_of_choco.goals.choice.RemoveVal;
import i_want_to_use_this_old_version_of_choco.goals.choice.SetVal;
import i_want_to_use_this_old_version_of_choco.goals.solver.ChoicePoint;
import i_want_to_use_this_old_version_of_choco.goals.solver.Sequence;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;

/**
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: 19 mai 2007
 * Time: 10:09:38
 * To change this template use File | Settings | File Templates.
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
