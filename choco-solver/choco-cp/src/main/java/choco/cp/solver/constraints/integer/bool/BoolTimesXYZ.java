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
package choco.cp.solver.constraints.integer.bool;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractTernIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * A simple Product constraint between boolean variables :
 * x * y = z
 */
public class BoolTimesXYZ extends AbstractTernIntSConstraint {

	/**
	 * A simple Product constraint between boolean variables :
	 * x * y = z
	 */
	public BoolTimesXYZ(IntDomainVar x0, IntDomainVar x1, IntDomainVar x2) {
		super(x0, x1, x2);
	}

	public String pretty() {
		return v0.pretty() + " * " + v1.pretty() + " = " + v2.pretty();
	}

    public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINTbitvector;
    }

    public void awakeOnInst(int idx) throws ContradictionException {
		int val;
		if (idx == 0) {
			val = v0.getVal();
			if (val == 0) {
				v2.instantiate(0, this, false);
			} else {
				if (v1.isInstantiatedTo(1)) {
					v2.instantiate(1, this, false);
				} else if (v2.isInstantiatedTo(0)) {
					v1.instantiate(0, this, false);
				}
			}
		} else if (idx == 1) {
			val = v1.getVal();
			if (val == 0) {
				v2.instantiate(0, this, false);
			} else {
				if (v0.isInstantiatedTo(1)) {
					v2.instantiate(1, this, false);
				} else if (v2.isInstantiatedTo(0)) {
					v0.instantiate(0, this, false);
				}
			}
		} else if (idx == 2) {
			val = v2.getVal();
			if (val == 1) {
				v0.instantiate(1, this, false);
				v1.instantiate(1, this, false);
			} else {
				if (v0.isInstantiatedTo(1)) {
					v1.instantiate(0, this, false);
				} else if (v1.isInstantiatedTo(1)) {
					v0.instantiate(0, this, false);
				}
			}
		}
	}

	public void propagate() throws ContradictionException {
		if (v0.isInstantiated()) awakeOnInst(0);
		if (v1.isInstantiated()) awakeOnInst(1);
		if (v2.isInstantiated()) awakeOnInst(2);
	}

	public boolean isSatisfied(int[] tuple) {
		return (tuple[0] * tuple[1] == tuple[2]);
	}

	public Boolean isEntailed() {
		if (v2.isInstantiatedTo(0)) {
			if (v0.isInstantiatedTo(0) ||
					v1.isInstantiatedTo(0)) {
				return Boolean.TRUE;
			} else if (v0.isInstantiatedTo(1) &&
					v1.isInstantiatedTo(1)) {
				return Boolean.FALSE;
			} else return null;
		} else if (v2.isInstantiatedTo(1)) {
			if (v0.isInstantiatedTo(0) ||
					v1.isInstantiatedTo(0)) {
				return Boolean.FALSE;
			} else if (v0.isInstantiatedTo(1) &&
					v1.isInstantiatedTo(1)) {
				return Boolean.TRUE;
			} else return null;
		} else return null;
	}

}
