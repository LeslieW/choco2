package i_want_to_use_this_old_version_of_choco.goals.choice;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.AbstractVar;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.branch.VarSelector;
import i_want_to_use_this_old_version_of_choco.goals.Goal;
import i_want_to_use_this_old_version_of_choco.goals.GoalHelper;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.search.MinDomain;
import i_want_to_use_this_old_version_of_choco.integer.search.ValIterator;
import i_want_to_use_this_old_version_of_choco.integer.search.ValSelector;

/**
 * Created by IntelliJ IDEA.
 * User: GROCHART
 * Date: 11 janv. 2008
 * Time: 21:09:00
 * To change this template use File | Settings | File Templates.
 */
public class Generate implements Goal {

	protected VarSelector varSelector;
  protected ValSelector valSelector;
  protected ValIterator valIterator;
  protected IntDomainVar[] vars;

  public Generate(IntDomainVar[] vars, VarSelector varSelector, ValIterator valIterator) {
    this(vars, varSelector);
    this.valIterator = valIterator;
  }

  public Generate(IntDomainVar[] vars, VarSelector varSelector, ValSelector valSelector) {
    this(vars, varSelector);
    this.valSelector = valSelector;
  }

  public Generate(IntDomainVar[] vars, VarSelector varSelector) {
    this.varSelector = varSelector;
    this.vars = new IntDomainVar[vars.length];
    System.arraycopy(vars, 0, this.vars, 0, vars.length);
  }

  public Generate(IntDomainVar[] vars) {
    this(vars, new MinDomain(null, vars));
  }

  public String pretty() {
    return "Generate";
  }

  public Goal execute(AbstractProblem problem) throws ContradictionException {
		AbstractVar var;
		var = varSelector.selectVar();
		if (var == null) {
      return null;
    } else {
      if (valSelector != null)
        return GoalHelper.and(new Instantiate((IntDomainVar) var, valSelector), this);
      else if (valIterator != null)
        return GoalHelper.and(new Instantiate((IntDomainVar) var, valIterator), this);
      else return GoalHelper.and(new Instantiate((IntDomainVar) var), this);
    }
	}
}
