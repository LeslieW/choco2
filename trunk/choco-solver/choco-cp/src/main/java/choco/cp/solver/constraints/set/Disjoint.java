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

import choco.cp.solver.variables.set.SetVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.set.AbstractBinSetSConstraint;
import choco.kernel.solver.variables.set.SetVar;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/**
 * A constraint stating that two sets have to be disjoint :
 * It is based on two propagation rules :
 * Env(v1) disjoint Ker(v2)
 * Env(v2) disjoint Ker(v1)
 */
public final class Disjoint extends AbstractBinSetSConstraint {

	public Disjoint(SetVar sv1, SetVar sv2) {
        super(sv1, sv2);
	}

    @Override
    public int getFilteredEventMask(int idx) {
        return SetVarEvent.ADDKER_MASK + SetVarEvent.INSTSET_MASK;
    }

    public void filter(int idx) throws ContradictionException {
		if (idx == 0) {
			DisposableIntIterator it1 = v0.getDomain().getKernelIterator();
            try{
                while (it1.hasNext()) {
                    v1.remFromEnveloppe(it1.next(), this, false);
                }
            }finally {
                it1.dispose();
            }
		} else if (idx == 1) {
			DisposableIntIterator it2 = v1.getDomain().getKernelIterator();
            try{
                while (it2.hasNext()) {
                    v0.remFromEnveloppe(it2.next(), this, false);
                }
            }finally {
                it2.dispose();
            }
		}
	}

	public void awakeOnKer(int varIdx, int x) throws ContradictionException {
		if (varIdx == 0) {
			v1.remFromEnveloppe(x, this, false);
		} else
			v0.remFromEnveloppe(x, this, false);
	}

	public void awakeOnInst(int varIdx) throws ContradictionException {
		filter(varIdx);
	}

	public void propagate() throws ContradictionException {
		filter(0);
		filter(1);
	}

	public boolean isSatisfied() {
		DisposableIntIterator it2 = v1.getDomain().getKernelIterator();
        try{
            while (it2.hasNext()) {
                if (v0.isInDomainKernel(it2.next())) return false;
            }
        }finally {
            it2.dispose();
        }
		return true;
	}

	public boolean isConsistent() {
		return isSatisfied();
	}

	public String toString() {
		return v0 + " disjoint " + v1;
	}

	public String pretty() {
		return v0.pretty() + " disjoint " + v1.pretty();
	}

	public Boolean isEntailed() {
		boolean someSureIn = false, somePossibleIn = false;
		DisposableIntIterator it1 = v0.getDomain().getEnveloppeIterator();
		while (it1.hasNext()) {
			int val = it1.next();
			if (v1.isInDomainEnveloppe(val)) {
				if (v0.isInDomainKernel(val) && v1.isInDomainKernel(val)) {
					someSureIn = true;
					break;
				}
				somePossibleIn = true;
			}
		}
      it1.dispose();
		if (someSureIn)
			return Boolean.FALSE;
		else if (!somePossibleIn)
			return Boolean.TRUE;
		else
			return null;
	}
}
