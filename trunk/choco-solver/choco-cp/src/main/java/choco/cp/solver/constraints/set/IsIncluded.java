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


/**
 * Specify a constraint to state x included y
 */
public class IsIncluded extends AbstractBinSetSConstraint {

	/**
	 * create a constraint to state sv1 isIncluded sv2
	 * sv1 isIncluded in sv2
	 * @param sv1
	 * @param sv2
	 */
	public IsIncluded(SetVar sv1, SetVar sv2) {
        super(sv1, sv2);
	}

	public void filter(int idx) throws ContradictionException {
		if (idx == 0) {
			DisposableIntIterator it1 = v0.getDomain().getKernelIterator();
			try{
                while (it1.hasNext()) {
                    v1.addToKernel(it1.next(), cIdx1);
                }
            }finally {
                it1.dispose();
            }
		} else if (idx == 1) {
			DisposableIntIterator it2 = v0.getDomain().getEnveloppeIterator();
			try{
                while (it2.hasNext()) {
                    int val = it2.next();
                    if (!v1.isInDomainEnveloppe(val)) {
                        v0.remFromEnveloppe(val, cIdx0);
                    }
                }
            }finally {
                it2.dispose();
            }
		}
	}

	@Override
	public void awakeOnEnv(int varIdx, int x) throws ContradictionException {
		if (varIdx == 1) {
			v0.remFromEnveloppe(x, cIdx0);
		}
	}

	@Override
	public void awakeOnKer(int varIdx, int x) throws ContradictionException {
		if (varIdx == 0) {
			v1.addToKernel(x, cIdx1);
		}
	}

	@Override
	public void awakeOnInst(int varIdx) throws ContradictionException {
		filter(varIdx);
	}

	public void propagate() throws ContradictionException {
		filter(0);
		filter(1);
	}

	public boolean isSatisfied() {
		DisposableIntIterator it2 = v0.getDomain().getKernelIterator();
        try{
            while (it2.hasNext()) {
                if (!v1.isInDomainKernel(it2.next())) {
                    return false;
                }
            }
        }finally {
            it2.dispose();
        }
		return true;
	}

	public boolean isConsistent() {
		return isSatisfied();
	}

	@Override
	public String toString() {
		return v0 + " disjoint " + v1;
	}

	@Override
	public String pretty() {
		return v0.pretty() + " disjoint " + v1.pretty();
	}

	@Override
	public Boolean isEntailed() {
		throw new UnsupportedOperationException("isEntailed not done on setvars");
	}
}

