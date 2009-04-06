package i_want_to_use_this_old_version_of_choco.goals.choice;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.branch.AbstractBranching;
import i_want_to_use_this_old_version_of_choco.goals.Goal;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: 19 mai 2007
 * Time: 10:28:16
 * To change this template use File | Settings | File Templates.
 */
public class SetVal implements Goal {
  protected static Logger logger = Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search.branching");

  protected IntDomainVar var;
  protected int val;

  public SetVal(IntDomainVar var, int val) {
    this.var = var;
    this.val = val;
  }

  public String pretty() {
    return var.pretty() + " <= " + val;
  }

  public Goal execute(AbstractProblem problem) throws ContradictionException {
    if (logger.isLoggable(Level.FINE)) {
			int n = problem.getEnvironment().getWorldIndex();
			if (n <= problem.getSolver().getSearchSolver().getLoggingMaxDepth()) {
				logger.log(Level.FINE, AbstractBranching.LOG_DOWN_MSG,
						new Object[]{new Integer(n), var, " == ", new Integer(val)});
			}
		}
    var.setVal(val);
    return null;
  }
}
