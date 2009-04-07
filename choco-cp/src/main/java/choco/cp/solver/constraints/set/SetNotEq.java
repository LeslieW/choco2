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

import choco.kernel.common.util.IntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.set.AbstractBinSetSConstraint;
import choco.kernel.solver.variables.set.SetVar;

/**
 * A constraint to state that two set vars can not be equal
 */
public class SetNotEq extends AbstractBinSetSConstraint {

	/**
	 * create a constraint to state sv1 is not equal to sv2
	 *
	 * @param sv1
	 * @param sv2
	 */
	public SetNotEq(SetVar sv1, SetVar sv2) {
        super(sv1, sv2);
        v0 = sv1;
		v1 = sv2;
	}

	public boolean checkAreEqual() throws ContradictionException {
		if (v0.isInstantiated() && v1.isInstantiated()
				&& v0.getKernelDomainSize() == v1.getKernelDomainSize()) {
			IntIterator it1 = v0.getDomain().getKernelIterator();
			while (it1.hasNext()) {
				if (!v1.isInDomainKernel(it1.next())) {
					return false;
				}
			}
			fail();
		}
		return false;
	}

	public boolean checkAreNotEqual(SetVar instVar, SetVar otherVar) {
		IntIterator it1 = instVar.getDomain().getKernelIterator();
		while (it1.hasNext()) {
			if (!otherVar.isInDomainEnveloppe(it1.next())) {
				return true;
			}
		}
		it1 = otherVar.getDomain().getKernelIterator();
		while (it1.hasNext()) {
			if (!instVar.isInDomainEnveloppe(it1.next())) {
				return true;
			}
		}

		return false;
	}


	public void filterForInst(SetVar instvar, SetVar otherVar, int idx) throws ContradictionException {
		int deltaSize = otherVar.getEnveloppeDomainSize() - otherVar.getKernelDomainSize();
		if (deltaSize == 0) {
			checkAreEqual();
		} else if (deltaSize == 1 && !checkAreNotEqual(instvar,otherVar)) {
			if (otherVar.getEnveloppeDomainSize() > instvar.getKernelDomainSize()) {
				//we need to add the element missing in otherVar, otherwise they will be equal
				IntIterator it1 = otherVar.getDomain().getEnveloppeIterator();
				while (it1.hasNext()) {
					int val = it1.next();
					if (!otherVar.isInDomainKernel(val)) {
						//System.out.println("add " + val + " : " + instvar + " " + otherVar);
						otherVar.addToKernel(val, idx);
					}
				}
			} else {
				//we need to remove the element missing in otherVar, otherwise they will be equal
				IntIterator it1 = otherVar.getDomain().getEnveloppeIterator();
				while (it1.hasNext()) {
					int val = it1.next();
					if (!otherVar.isInDomainKernel(val)) {
						//System.out.println("rem " + val + " : " + instvar + " " + otherVar);
						otherVar.remFromEnveloppe(val, idx);
					}
				}
			}
		}
	}

	@Override
	public void awakeOnKer(int varIdx, int x) throws ContradictionException {
		if (varIdx == 0) {
			if (!v1.isInDomainEnveloppe(x)) {
				setPassive();
			} else if (v1.isInstantiated()) {
				filterForInst(v1, v0, 0);
			}
		} else {
			if (!v0.isInDomainEnveloppe(x)) {
				setPassive();
			} else if (v0.isInstantiated()) {
				filterForInst(v0, v1, 1);
			}
		}
	}

	@Override
	public void awakeOnEnv(int varIdx, int x) throws ContradictionException {
		if (varIdx == 0) {
			if (v1.isInDomainKernel(x)) {
				setPassive();
			} else if (v1.isInstantiated()) {
				filterForInst(v1, v0, 0);
			}
		} else {
			if (v0.isInDomainKernel(x)) {
				setPassive();
			} else if (v0.isInstantiated()) {
				filterForInst(v0, v1, 1);
			}
		}
	}

	@Override
	public void awakeOnInst(int varIdx) throws ContradictionException {
		if (varIdx == 0) {
			filterForInst(v0, v1, 1);
		} else {
			filterForInst(v1, v0, 0);
		}
	}

	public void propagate() throws ContradictionException {
		if (v0.isInstantiated()) {
			filterForInst(v0, v1, 1);
		}
		if (v1.isInstantiated()) {
			filterForInst(v1, v0, 0);
		}
	}

	public boolean isSatisfied() {
		if (v0.isInstantiated() && v1.isInstantiated()
				&& v0.getKernelDomainSize() == v1.getKernelDomainSize()) {
			IntIterator it1 = v0.getDomain().getKernelIterator();
			while (it1.hasNext()) {
				if (!v1.isInDomainKernel(it1.next())) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean isConsistent() {
		return isSatisfied();
	}

	@Override
	public String toString() {
		return v0 + " neq " + v1;
	}

	@Override
	public String pretty() {
		return v0.pretty() + " neq " + v1.pretty();
	}

	@Override
	public Boolean isEntailed() {
		throw new UnsupportedOperationException("isEntailed not done on setvars");
	}
}
