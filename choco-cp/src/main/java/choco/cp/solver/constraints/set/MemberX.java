/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.constraints.set;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.set.AbstractUnSetSConstraint;
import choco.kernel.solver.variables.set.SetVar;

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
public class MemberX extends AbstractUnSetSConstraint {

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
