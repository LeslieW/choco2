package i_want_to_use_this_old_version_of_choco.reified;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;

/**
 * Created by IntelliJ IDEA.
 * User: narendra
 * Date: 14 janv. 2008
 * Time: 16:40:12
 */

public class BinaryDisjunction extends AbstractBinaryReifiedConstraint {

	public BinaryDisjunction(AbstractConstraint c1, AbstractConstraint c2) {
		super(c1, c2);
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * Pretty print of the constraint.
	 */
	public String pretty() {
		return " (" + const0.pretty() + ") OR (" + const1.pretty() + ") ";
	}

	public void propagate() throws ContradictionException {

		if (const0.isCompletelyInstantiated() && const1.isCompletelyInstantiated()
				&& !const0.isSatisfied() && !const1.isSatisfied()) {
			throw new ContradictionException(this.getProblem());
		}
	}

	public boolean isConsistent() {
		return const0.isConsistent() || const1.isConsistent();
	}

	public boolean isSatisfied() {
		return const0.isSatisfied() || const1.isSatisfied();
	}

	public boolean isEquivalentTo(Constraint compareTo) {
		if (compareTo instanceof BinaryDisjunction) {
			BinaryDisjunction c = (BinaryDisjunction) compareTo;
			return ((this.const0.isEquivalentTo(c.const0) && this.const1.isEquivalentTo(c.const1)) ||
					(this.const0.isEquivalentTo(c.const1) && this.const1.isEquivalentTo(c.const0)));
		} else {
			return false;
		}
	}
}
