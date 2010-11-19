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
import choco.kernel.solver.constraints.set.AbstractTernSetSConstraint;
import choco.kernel.solver.variables.set.SetVar;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/**
 * A constraint stating that a set is the intersection of two others
 * There are seven propagation rules for the constraint sv3 = intersection(sv1, sv2)
 * Ker(sv1) contains Ker(sv3)
 * Ker(sv2) contains Ker(sv3)
 * Ker(sv3) contains (Ker(sv1) inter Ker(sv2))
 * Env(v3)  disjoint Complement(Env(v1))
 * Env(v3)  disjoint Complement(Env(v2))
 * Env(v2)  disjoint Ker(v1) inter Complement(Env(v3))
 * Env(v1)  disjoint Ker(v2) inter Complement(Env(v3))
 */
public final class SetIntersection extends AbstractTernSetSConstraint {

	/**
	 * @param sv3 the intersection set
	 */

	public SetIntersection(SetVar sv1, SetVar sv2, SetVar sv3) {
        super(sv1, sv2, sv3);
	}

    @Override
    public int getFilteredEventMask(int idx) {
        return SetVarEvent.ADDKER_MASK + SetVarEvent.REMENV_MASK + SetVarEvent.INSTSET_MASK;
    }

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public void awakeOnKer(int varIdx, int x) throws ContradictionException {
		if (varIdx == 0) {
			if (v1.isInDomainKernel(x)) v2.addToKernel(x, this, false);
			if (!v2.isInDomainEnveloppe(x)) v1.remFromEnveloppe(x, this, false);
		} else if (varIdx == 1) {
			if (v0.isInDomainKernel(x)) v2.addToKernel(x, this, false);
			if (!v2.isInDomainEnveloppe(x)) v0.remFromEnveloppe(x, this, false);
		} else {
			if (!v0.isInDomainKernel(x)) v0.addToKernel(x, this, false);
			if (!v1.isInDomainKernel(x)) v1.addToKernel(x, this, false);
		}
	}

	public void awakeOnEnv(int varIdx, int x) throws ContradictionException {
		if (varIdx == 0) {
			v2.remFromEnveloppe(x, this, false);
		} else if (varIdx == 1) {
			v2.remFromEnveloppe(x, this, false);
		} else {
			if (v0.isInDomainKernel(x)) v1.remFromEnveloppe(x, this, false);
			if (v1.isInDomainKernel(x)) v0.remFromEnveloppe(x, this, false);
		}
	}

	public void awakeOnInst(int varIdx) throws ContradictionException {
		int x;
		if (varIdx == 0) {
			DisposableIntIterator it = v0.getDomain().getKernelIterator();
            try{
                while (it.hasNext()) {
                    x = it.next();
                    if (v1.isInDomainKernel(x)) v2.addToKernel(x, this, false);
                    if (!v2.isInDomainEnveloppe(x)) v1.remFromEnveloppe(x, this, false);
                }
            }finally {
                it.dispose();
            }
			it = v2.getDomain().getEnveloppeIterator();
            try{
                while (it.hasNext()) {
                    x = it.next();
                    if (!v0.isInDomainKernel(x)) v2.remFromEnveloppe(x, this, false);
                }
            }finally {
                it.dispose();
            }
		} else if (varIdx == 1) {
			DisposableIntIterator it = v1.getDomain().getKernelIterator();
            try{
			while (it.hasNext()) {
				x = it.next();
				if (v0.isInDomainKernel(x)) v2.addToKernel(x, this, false);
				if (!v2.isInDomainEnveloppe(x)) v0.remFromEnveloppe(x, this, false);
			}
            }finally {
                it.dispose();
            }
			it = v2.getDomain().getEnveloppeIterator();
            try{
			while (it.hasNext()) {
				x = it.next();
				if (!v1.isInDomainKernel(x)) v2.remFromEnveloppe(x, this, false);
			}
            }finally {
                it.dispose();
            }
		} else {
			DisposableIntIterator it = v2.getDomain().getKernelIterator();
            try{
                while (it.hasNext()) {
                    x = it.next();
                    if (!v0.isInDomainKernel(x)) v0.addToKernel(x, this, false);
                    if (!v1.isInDomainKernel(x)) v1.addToKernel(x, this, false);
                }
            }finally {
                it.dispose();
            }
		}
	}

	public void propagate() throws ContradictionException {
		DisposableIntIterator it = v0.getDomain().getKernelIterator();
		try{
        while (it.hasNext()) {
			int val = it.next();
			if (v1.isInDomainKernel(val)) v2.addToKernel(val, this, false);
			if (!v2.isInDomainEnveloppe(val)) v1.remFromEnveloppe(val, this, false);
		}
        }finally {
            it.dispose();
        }
		it = v1.getDomain().getKernelIterator();
        try{
            while (it.hasNext()) {
                int val = it.next();
                if (v0.isInDomainKernel(val)) v2.addToKernel(val, this, false);
                if (!v2.isInDomainEnveloppe(val)) v0.remFromEnveloppe(val, this, false);
            }
        }finally {
            it.dispose();
        }
		it = v2.getDomain().getKernelIterator();
        try{
            while (it.hasNext()) {
                int val = it.next();
                if (!v0.isInDomainKernel(val)) v0.addToKernel(val, this, false);
                if (!v1.isInDomainKernel(val)) v1.addToKernel(val, this, false);
            }
        }finally {
            it.dispose();
        }
		it = v2.getDomain().getEnveloppeIterator();
        try{
            while (it.hasNext()) {
                int val = it.next();
                if (!v0.isInDomainEnveloppe(val) ||
                        !v1.isInDomainEnveloppe(val))
                    v2.remFromEnveloppe(val, this, false);

            }           
        }finally {
            it.dispose();
        }
	}

	public String toString() {
		return v0 + " intersect " + v1 + " = " + v2;
	}

	public String pretty() {
		return v0.pretty() + " intersect " + v1.pretty() + " = " + v2.pretty();
	}

	public boolean isSatisfied() {
		boolean nonout = true, allIn = true;
		DisposableIntIterator it = v2.getDomain().getKernelIterator();
		while (it.hasNext()) {
			int val = it.next();
			if (!(v0.isInDomainKernel(val)) || !(v1.isInDomainKernel(val))) {
				allIn = false;
				break;
			}
		}
        it.dispose();
		if (!allIn) return false;
		it = v1.getDomain().getKernelIterator();
		while (it.hasNext()) {
			int val = it.next();
			if (!v2.isInDomainKernel(val) && v0.isInDomainKernel(val)) {
				nonout = false;
				break;
			}
		}
        it.dispose();
		return nonout;
	}

	public boolean isConsistent() {
		// TODO
		return false;
	}
}
