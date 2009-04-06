package i_want_to_use_this_old_version_of_choco.goals.choice;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.goals.Goal;
import i_want_to_use_this_old_version_of_choco.goals.GoalHelper;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.search.MinVal;
import i_want_to_use_this_old_version_of_choco.integer.search.ValIterator;
import i_want_to_use_this_old_version_of_choco.integer.search.ValSelector;

/**
 * Created by IntelliJ IDEA.
 * User: GROCHART
 * Date: 11 janv. 2008
 * Time: 21:11:21
 * To change this template use File | Settings | File Templates.
 */
public class Instantiate implements Goal {

	protected IntDomainVar var;
	protected ValSelector valSelector;
  protected ValIterator valIterator;
  protected int previousVal = Integer.MAX_VALUE;

  public Instantiate(IntDomainVar var, ValSelector s) {
		this.var = var;
		this.valSelector = s;
	}

  public Instantiate(IntDomainVar var, ValIterator valIterator) {
    this.var = var;
		this.valIterator = valIterator;  
  }

  public Instantiate(IntDomainVar var) {
		this.var = var;
		this.valSelector = new MinVal();
	}

  public String pretty() {
    return "Instantiate " + var.pretty();
  }

  public Goal execute(AbstractProblem problem) throws ContradictionException {
		if (var.isInstantiated()) return null;
    int val = -1;
    if (valIterator != null) {
      if (previousVal == Integer.MAX_VALUE) {
        val = valIterator.getFirstVal(var);
      } else {
        if (valIterator.hasNextVal(var, previousVal))
          val = valIterator.getNextVal(var, previousVal);
      }
      previousVal = val;
    } else {
      val = valSelector.getBestVal(var);
    }
    return GoalHelper.or(GoalHelper.setVal(var, val),
				GoalHelper.and(GoalHelper.remVal(var, val), this));
	}
}
