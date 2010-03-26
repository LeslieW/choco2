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

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.set.AbstractBinSetSConstraint;
import choco.kernel.solver.variables.set.SetVar;


/* 
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 27 avr. 2008
 * Since : Choco 2.0.0
 *
 */
public final class IsNotIncluded extends AbstractBinSetSConstraint {

	/**
	 * create a constraint to state sv1 isIncluded sv2
	 * sv1 isIncluded in sv2
	 *
	 * @param sv1
	 * @param sv2
	 */
	public IsNotIncluded(SetVar sv1, SetVar sv2) {
        super(sv1, sv2);
	}

	public boolean isKer1IncludedInKer2(SetVar x0, SetVar x1) {
		if (x0.getKernelDomainSize() <= x1.getKernelDomainSize()) {
			DisposableIntIterator it1 = x0.getDomain().getKernelIterator();
			while (it1.hasNext()) {
				if (!x1.isInDomainKernel(it1.next())) {
                    it1.dispose();
					return false;
				}
			}
            it1.dispose();
			return true;
		} else {
			return false;
		}
	}

	public boolean isKer1IncludedInEnv2(SetVar x0, SetVar x1) {
		if (x0.getKernelDomainSize() <= x1.getEnveloppeDomainSize()) {
			DisposableIntIterator it1 = x0.getDomain().getKernelIterator();
			while (it1.hasNext()) {
				if (!x1.isInDomainEnveloppe(it1.next())) {
                    it1.dispose();
					return false;
				}
			}
            it1.dispose();
			return true;
		} else {
			return false;
		}
	}

	public boolean prune;

	/**
	 * test if all values in env(v0) are in ker(v1) and returns
	 * - +Infini if it is false and there are at least two such values
	 * - -Infini if it is true
	 * - the single value that is in env(v0) and not in ker(v1) if there is a single one
	 */
	public int findUniqueOutsider() throws ContradictionException {
		prune = false;
		DisposableIntIterator it1 = v0.getDomain().getEnveloppeIterator();
		int uniqueOutsider = Integer.MAX_VALUE;
		while (it1.hasNext()) {
			int val = it1.next();
			if (!v1.isInDomainKernel(val)) {
				if (!prune) {
					uniqueOutsider = val;
					prune = true;
				} else {
					prune = false;
                    it1.dispose();
					return Integer.MAX_VALUE;
				}
			}
		}
        it1.dispose();
		if (!prune) {
			this.fail();
		}
		return uniqueOutsider;
	}


	public void filter() throws ContradictionException {
		int uniqueOutsider = findUniqueOutsider();
		if (prune) {
			if (v0.isInDomainKernel(uniqueOutsider)) {
				v1.remFromEnveloppe(uniqueOutsider, this, true);
			}
			if (!v1.isInDomainEnveloppe(uniqueOutsider)) {
				v0.addToKernel(uniqueOutsider, this, true);
			}
		}
	}

	@Override
	public void awakeOnEnv(int varIdx, int x) throws ContradictionException {
		if (varIdx == 1) {
			if (v0.isInDomainKernel(x)) {
				setPassive();
			}
		}
		filter();
	}

	@Override
	public void awakeOnKer(int varIdx, int x) throws ContradictionException {
		if (varIdx == 0) {
			if (!v1.isInDomainEnveloppe(x)) {
				setPassive();
			}
		}
		filter();
	}

	@Override
	public void awakeOnInst(int varIdx) throws ContradictionException {
		filter();
	}

	public void propagate() throws ContradictionException {
		filter();
	}

	public boolean isSatisfied() {
		DisposableIntIterator it2 = v0.getDomain().getKernelIterator();
		while (it2.hasNext()) {
			if (!v1.isInDomainKernel(it2.next())) {
                it2.dispose();
				return true;
			}
		}
        it2.dispose();
		return false;
	}

	public boolean isConsistent() {
		return isSatisfied();
	}

	@Override
	public String toString() {
		return v0 + " is Not Included in " + v1;
	}

	@Override
	public String pretty() {
		return v0.pretty() + " is Not Included in " + v1.pretty();
	}

	@Override
	public Boolean isEntailed() {
		throw new UnsupportedOperationException("isEntailed not done on setvars");
	}
}
