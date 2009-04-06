package i_want_to_use_this_old_version_of_choco.set.constraint;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.set.SetVar;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/**
 * Ensure that an int variable does not belong to a set variable
 */
public class NotMemberXY extends AbstractBinSetIntConstraint {

	public NotMemberXY(SetVar set, IntDomainVar iv) {
		super(iv, set);
	}

	/**
	 * if only one value out of the kernel, then this value can be removed from the enveloppe
	 *
	 * @throws ContradictionException
	 */
	public void filter() throws ContradictionException {
		IntIterator it = v0.getDomain().getIterator();
		int count = 0, val = Integer.MAX_VALUE;
		while (it.hasNext()) {
			val = it.next();
			if (!v1.isInDomainKernel(val)) {
				count += 1;
				if (count > 1) break;
			}
		}
		if (count == 0)
			this.fail();
		else if (count == 1) {
			v0.instantiate(val, cIdx0);
			v1.remFromEnveloppe(val, cIdx1);
		}
	}

	public void awakeOnInf(int idx) throws ContradictionException {
		filter();
	}

	public void awakeOnSup(int idx) throws ContradictionException {
		filter();
	}

	public void awakeOnRem(int idx, int x) throws ContradictionException {
		filter();
	}

	public void awakeOnKer(int varIdx, int x) throws ContradictionException {
		v0.removeVal(x, cIdx0);
		filter();
	}

	public void awakeOnEnv(int varIdx, int x) throws ContradictionException {
		filter();
	}

	public void awakeOnInst(int varIdx) throws ContradictionException {
		if (varIdx == 0)
			v1.remFromEnveloppe(v0.getVal(), cIdx1);
		else {
			IntIterator it = v1.getDomain().getKernelIterator();
			while (it.hasNext()) {
				v0.removeVal(it.next(), cIdx0);
			}
			filter();
		}
	}


	public void propagate() throws ContradictionException {
		IntIterator it = v1.getDomain().getKernelIterator();
		while (it.hasNext()) {
			v0.removeVal(it.next(), cIdx0);
		}
		filter();
	}

	public boolean isSatisfied() {
		return !v1.isInDomainKernel(v0.getVal());
	}

	public boolean isConsistent() {
		IntIterator it = v0.getDomain().getIterator();
		while (it.hasNext()) {
			if (v1.isInDomainKernel(it.next())) return false;
		}
		return true;
	}

	public String toString() {
		return v0 + " is not in " + v1;
	}

	public String pretty() {
		return v0.pretty() + " is not in " + v1.pretty();
	}

	public Boolean isEntailed() {
		boolean allValuesOutEnv = true, allValuesInKer = true;
		IntIterator it = v0.getDomain().getIterator();
		while (it.hasNext()) {
			int val = it.next();
			if (v1.isInDomainEnveloppe(val)) {
				allValuesOutEnv = false;
				if (v1.isInDomainKernel(val)) {
					allValuesInKer = false;
					break;
				}
			}
		}
		if (allValuesInKer)
			return Boolean.FALSE;
		else if (allValuesOutEnv)
			return Boolean.TRUE;
		else
			return null;
	}
}
