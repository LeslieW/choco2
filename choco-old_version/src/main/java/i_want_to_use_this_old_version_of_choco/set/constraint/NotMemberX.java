package i_want_to_use_this_old_version_of_choco.set.constraint;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.set.SetVar;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/**
 * Ensure that a value will not belong to a setVar
 */
public class NotMemberX extends AbstractUnSetConstraint {

	protected int cste;

	public NotMemberX(SetVar v, int val) {
		super(v);
		cste = val;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public void propagate() throws ContradictionException {
		v0.remFromEnveloppe(cste, cIdx0);
	}

	public void awakeOnInst(int varIdx) throws ContradictionException {
		if (v0.isInDomainKernel(cste))
			this.fail();
	}

	public void awakeOnEnv(int varIdx, int x) throws ContradictionException {
		if (!v0.isInDomainEnveloppe(cste))
			setEntailed();
	}

	public void awakeOnKer(int varIdx, int x) throws ContradictionException {
		if (v0.isInDomainKernel(cste))
			this.fail();
	}

	public boolean isSatisfied() {
		return !v0.isInDomainEnveloppe(cste);
	}

	public boolean isConsistent() {
		return !v0.isInDomainEnveloppe(cste);
	}

	public String toString() {
		return cste + " is not in " + v0;
	}

	public String pretty() {
		return cste + " is not in " + v0.pretty();
	}


	/**
	 * Checks if the listeners must be checked or must fail.
	 */
	public Boolean isEntailed() {
		if (!v0.isInDomainEnveloppe(cste))
			return Boolean.TRUE;
		else if (v0.isInDomainKernel(cste))
			return Boolean.FALSE;
		else
			return null;
	}

}
