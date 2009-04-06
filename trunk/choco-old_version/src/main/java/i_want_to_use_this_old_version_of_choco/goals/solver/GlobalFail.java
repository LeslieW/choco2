package i_want_to_use_this_old_version_of_choco.goals.solver;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.goals.Goal;

/**
 * Created by IntelliJ IDEA.
 * User: GROCHART
 * Date: 11 janv. 2008
 * Time: 21:29:59
 * To change this template use File | Settings | File Templates.
 */
public class GlobalFail implements Goal {
  public String pretty()
  {
    return "Fail";
  }

  public Goal execute(AbstractProblem problem) throws ContradictionException {
    ((GoalSearchSolver) problem.getSolver().getSearchSolver()).setGlobalContradiction();
    throw new ContradictionException(problem);
  }
}
