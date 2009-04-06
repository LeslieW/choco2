package i_want_to_use_this_old_version_of_choco.reified;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.integer.IntConstraint;
import i_want_to_use_this_old_version_of_choco.real.constraint.RealConstraint;
import i_want_to_use_this_old_version_of_choco.set.SetConstraint;

/**
 * Created by IntelliJ IDEA.
 * User: narendra
 * Date: 14 janv. 2008
 * Time: 16:48:05
 */
public abstract class AbstractReifiedConstraint extends AbstractConstraint implements IntConstraint, RealConstraint, SetConstraint{

    /**
     * This function connects a constraint with its variables in several ways.
     * Note that it may only be called once the constraint
     * has been fully created and is being posted to a problem.
     * Note that it should be called only once per constraint.
     * This can be a dynamic addition (undone upon backtracking) or not
     *
     * @param dynamicAddition if the addition should be dynamical
     */
    public void addListener(boolean dynamicAddition) {
        assignIndices(this, -1, dynamicAddition);
        active = this.getProblem().getEnvironment().makeBool(true);
        if (this.hook != null) this.hook.addListener();
    }

    public abstract Constraint getSubConstraint(final int constIdx);

    public abstract int getNbSubConstraints();

    public abstract int getSubConstraintIdx(final int varIdx);

	public boolean isSatisfied(int[] tuple) {
		throw new Error(this + " needs to implement isSatisfied(int[] tuple) to be embedded in reified constraints");
	}

}
