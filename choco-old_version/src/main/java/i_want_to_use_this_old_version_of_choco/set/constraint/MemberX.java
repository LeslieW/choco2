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
 * Enforce a value to belong to a setVar
 */
public class MemberX extends AbstractUnSetConstraint {

	protected int cste;

	public MemberX(SetVar v, int val) {
		super(v);
		cste = val;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public void awake() throws ContradictionException {
		propagate();
	}

	public void propagate() throws ContradictionException {
		v0.addToKernel(cste, cIdx0);
	}

	public void awakeOnInst(int varIdx) throws ContradictionException {
		if (!v0.isInDomainKernel(cste))
			this.fail();
	}

	public void awakeOnEnv(int varIdx, int x) throws ContradictionException {
		if (!v0.isInDomainEnveloppe(cste))
			this.fail();
	}

	public void awakeOnKer(int varIdx, int x) throws ContradictionException {
		if (v0.isInDomainKernel(cste)) setEntailed();
	}

	public boolean isSatisfied() {
		return v0.isInDomainKernel(cste);
	}

	public boolean isConsistent() {
		return v0.isInDomainKernel(cste);
	}

	public String toString() {
		return cste + " is in " + v0;
	}

	public String pretty() {
		return cste + " is in " + v0;
	}

	/**
	 * Checks if the listeners must be checked or must fail.
	 */
	public Boolean isEntailed() {
		if (v0.isInDomainKernel(cste))
			return Boolean.TRUE;
		else if (!v0.isInDomainEnveloppe(cste))
			return Boolean.FALSE;
		else
			return null;
	}

}
