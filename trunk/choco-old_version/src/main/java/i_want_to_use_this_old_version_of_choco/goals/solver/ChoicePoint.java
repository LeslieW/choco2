package i_want_to_use_this_old_version_of_choco.goals.solver;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.goals.Goal;

/**
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: 19 mai 2007
 * Time: 09:31:11
 * To change this template use File | Settings | File Templates.
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

  public Goal execute(AbstractProblem problem) throws ContradictionException {
    System.err.println("Should not be called in ChoicePoint !!");
    return null;
  }

  public Goal getChoice(int choiceIndex) {
    return choices[choiceIndex];
  }

  public int getNbChoices() {
    return choices.length;
  }
}
