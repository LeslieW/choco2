package i_want_to_use_this_old_version_of_choco.goals;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ContradictionException;

/**
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: 19 mai 2007
 * Time: 09:16:09
 * To change this template use File | Settings | File Templates.
 */
public interface Goal {
  public Goal execute(AbstractProblem problem) throws ContradictionException;
  public String pretty();
}
