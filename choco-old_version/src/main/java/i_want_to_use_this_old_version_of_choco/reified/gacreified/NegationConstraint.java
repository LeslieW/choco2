package i_want_to_use_this_old_version_of_choco.reified.gacreified;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Var;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.constraints.AbstractIntConstraint;
import i_want_to_use_this_old_version_of_choco.reified.AbstractReifiedConstraint;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

/**
 * The Negation of a constraint switch to generate and test if posted as a constraint
 * but is intended to be used in reified constraint in a predicat to achieve AC
 **/
public class NegationConstraint extends AbstractIntConstraint {

	AbstractIntConstraint const0;

	public NegationConstraint(AbstractConstraint constraint) {
		const0 = (AbstractIntConstraint) constraint;
	}

	public String pretty() {
		return " NOT (" + const0.pretty() + ") ";
	}

	public void propagate() throws ContradictionException {
		if (const0.isCompletelyInstantiated() && const0.isSatisfied()) {
			this.fail();
		}
	}

	public boolean isConsistent() {
		return !const0.isConsistent();
	}

	public boolean isSatisfied() {
		return !const0.isSatisfied();
	}

	public boolean isSatisfied(int[] tuple) {
		return !const0.isSatisfied(tuple);
	}

	public boolean isEquivalentTo(Constraint compareTo) {
		throw new Error("isEquivalent not implemented on negation");
	}

	public int getNbVars() {
		return const0.getNbVars();
	}

	public Var getVar(int i) {
		return const0.getVar(i);
	}

	public IntDomainVar getIntVar(int i) {
		return const0.getIntVar(i);
	}

	/**
	 * Builds a copy of this constraint.
	 *
	 * @return a copy of this constraint
	 * @throws CloneNotSupportedException if an problem occurs when cloning
	 *                                    elements pf this constraint
	 */
	public Object clone() throws CloneNotSupportedException {
		NegationConstraint newc =
				(NegationConstraint) super.clone();
		newc.const0 = (AbstractIntConstraint) this.const0.clone();
		return newc;
	}

	/**
	 * Assigns indices to variables for the global constraint involving
	 * this one.
	 *
	 * @param root            the global constraint including this one
	 * @param i               the first available index
	 * @param dynamicAddition states if the constraint is added definitively
	 * @return the next available index for the global constraint
	 */
	public int assignIndices(final AbstractReifiedConstraint root,
	                         final int i, final boolean dynamicAddition) {
		int j = i;
		j = const0.assignIndices(root, j, dynamicAddition);
		return j;
	}

	/**
	 * Sets the variable i.
	 *
	 * @param i the variable index
	 * @param v the variable
	 */
	public void setVar(final int i, final Var v) {
		const0.setVar(i, v);
	}

	/**
	 * Checks if all variables are instantiated, that if sub-constraint
	 * variables are instantiated.
	 *
	 * @return true if all variables are instantiated
	 */
	public boolean isCompletelyInstantiated() {
		return const0.isCompletelyInstantiated();
	}


	/**
	 * Returns the constraint index according to the variable i.
	 *
	 * @param i the variable index
	 * @return this constraint index according to the variable
	 */
	public int getConstraintIdx(final int i) {
		return const0.getConstraintIdx(i);
	}

	/**
	 * Sets the constraint index according to the variable i.
	 *
	 * @param i   the variable index
	 * @param idx the requested constraint index
	 */
	public void setConstraintIndex(final int i, final int idx) {
		const0.setConstraintIndex(i, idx);
	}

	public void awakeOnRemovals(int varIdx, IntIterator deltaDomain) throws ContradictionException {
		this.constAwake(false);
	}

	public void awakeOnBounds(int varIdx) throws ContradictionException {
		this.constAwake(false);
	}

	public void awakeOnInf(int varIdx) throws ContradictionException {
		this.constAwake(false);
	}

	public void awakeOnSup(int varIdx) throws ContradictionException {
		this.constAwake(false);
	}

	public void awakeOnInst(int varIdx) throws ContradictionException {
		this.constAwake(false);
	}

	public void awakeOnRem(int varIdx, int val) throws ContradictionException {
		this.constAwake(false);
	}

}
