package i_want_to_use_this_old_version_of_choco.goals.choice;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.branch.AbstractIntBranching;
import i_want_to_use_this_old_version_of_choco.goals.Goal;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: 19 mai 2007
 * Time: 10:24:06
 * To change this template use File | Settings | File Templates.
 */
public class RemoveVal implements Goal {
  protected static Logger logger = Logger.getLogger("i_want_to_use_this_old_version_of_choco.search.branching");
  protected IntDomainVar var;
  protected int val;

  public RemoveVal(IntDomainVar var, int val) {
    this.var = var;
    this.val = val;
  }

  public String pretty() {
    return var.pretty() + " != " + val;
  }

  public Goal execute(AbstractProblem problem) throws ContradictionException {
    if (logger.isLoggable(Level.FINE)) {
		  int n = problem.getEnvironment().getWorldIndex();
		  if (n <= problem.getSolver().getSearchSolver().getLoggingMaxDepth()) {
		    logger.log(Level.FINE, AbstractIntBranching.LOG_UP_MSG, new Object[]{new Integer(n + 1), var, " != ", new Integer(val)});
		  }
		}
    var.remVal(val);
    return null;
  }
}
