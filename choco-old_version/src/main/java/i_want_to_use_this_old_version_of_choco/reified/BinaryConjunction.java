package i_want_to_use_this_old_version_of_choco.reified;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 25 janv. 2008
 * Time: 15:00:37
 */
public class BinaryConjunction extends AbstractBinaryReifiedConstraint{

    public BinaryConjunction(AbstractConstraint c1, AbstractConstraint c2) {
    super(c1, c2);
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  /**
   * Pretty print of the constraint.
   */

  public String pretty() {
    return " (" + const0.pretty() + ") AND (" + const1.pretty() + ") ";
  }

    public void propagate() throws ContradictionException {

        if (   (const0.isCompletelyInstantiated() && !const0.isSatisfied())
            || (const1.isCompletelyInstantiated() && !const1.isSatisfied()) ) {
            throw new ContradictionException(this.getProblem());
        }
    }

    public boolean isConsistent() {
        return const0.isConsistent() && const1.isConsistent();
    }

    public boolean isSatisfied() {
        return const0.isSatisfied() && const1.isSatisfied();
    }

    public boolean isEquivalentTo(Constraint compareTo) {
    if (compareTo instanceof BinaryConjunction) {
      BinaryConjunction c = (BinaryConjunction) compareTo;
      return ((this.const0.isEquivalentTo(c.const0) && this.const1.isEquivalentTo(c.const1)) ||
          (this.const0.isEquivalentTo(c.const1) && this.const1.isEquivalentTo(c.const0)));
    } else {
      return false;
    }
  }
}
