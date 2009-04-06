package i_want_to_use_this_old_version_of_choco.reified;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntConstraint;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 25 janv. 2008
 * Time: 09:44:52
 */
public class Negation extends AbstractUnaryReifiedConstraint {

    public Negation(AbstractConstraint constraint) {
        super(constraint);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String pretty() {
        return " NOT (" + const0.pretty() + ") ";
    }

    public void propagate() throws ContradictionException {
        if (const0.isCompletelyInstantiated() && const0.isSatisfied()) {
            throw new ContradictionException(this.getProblem());
        }
    }

    public boolean isConsistent() {
        return !const0.isConsistent();
    }

    public boolean isSatisfied() {
        return !const0.isSatisfied();
    }

	public boolean isSatisfied(int[] tuple) {
		return !((IntConstraint) const0).isSatisfied(tuple);
	}

    public boolean isEquivalentTo(Constraint compareTo) {
        if (compareTo instanceof Negation) {
            Negation c = (Negation) compareTo;
            return ((this.const0.isEquivalentTo(c.const0)));
        } else {
            return false;
        }
    }

}
