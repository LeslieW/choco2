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
package choco.cp.solver.constraints.integer.bool.sum;

import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * A special case of sums over boolean variables only
 */
public final class BoolSumStructure {

	protected final IntDomainVar[] vars;
	
	protected final AbstractSConstraint<?> cstr;
	/**
	 * The number of variables instantiated to zero in the sum
	 */
	public final IStateInt nbz;

	/**
	 * The number of variables instantiated to one in the sum
	 */
	public final IStateInt nbo;

	public final int bGap;

	public final int bValue;

	public BoolSumStructure(IEnvironment environment, AbstractSConstraint<?> cstr, IntDomainVar[] vars, int bValue) {
		super();
		this.cstr = cstr;
		for (IntDomainVar var : vars) {
			if( ! var.hasBooleanDomain() ) throw new SolverException("BoolSum takes only boolean variables: "+var.pretty());
		}
		this.vars = vars;
		this.bValue = bValue;
		this.bGap = vars.length - bValue;
		nbz = environment.makeInt(0);
		nbo = environment.makeInt(0);
	}

	
	public final IntDomainVar[] getBoolVars() {
		return vars;
	}


	public final IStateInt getNbZero() {
		return nbz;
	}


	public final IStateInt getNbOne() {
		return nbo;
	}


	public final int getbGap() {
		return bGap;
	}


	public final int getbValue() {
		return bValue;
	}


	public final void reset() {
		nbz.set(0);
		nbo.set(0);
	}

	public final boolean filterLeq() throws ContradictionException {
		if(bValue == 0) {
			putAllZero();
			return false;
		}
		return true;
	}
	
	public final boolean filterGeq() throws ContradictionException {
		if(bValue == vars.length) {
			putAllOne();
			return false;
		}
		return true;
	}
	
	public final void putAllZero() throws ContradictionException {
		for (int i = 0; i < vars.length; i++) {
			if (!vars[i].isInstantiated())
				vars[i].instantiate(0, cstr, false);
		}
		cstr.setEntailed();
	}

	public final void putAllOne() throws ContradictionException {
		for (int i = 0; i < vars.length; i++) {
			if (!vars[i].isInstantiated())
				vars[i].instantiate(1, cstr, false);
		}
		cstr.setEntailed();
	}
	

	public final void addOne() {
		nbo.add(1);
	}
	
	public final void addZero() {
		nbz.add(1);
	}
	
	public void awakeOnEq() throws ContradictionException {
		if (nbo.get() > bValue || nbz.get() > bGap) {
			cstr.fail();
		} else if (nbo.get() == bValue) {
			putAllZero();
		} else if (nbz.get() == bGap) {
			putAllOne();
		}
	}
	
	public void awakeOnGeq() throws ContradictionException {
		if (nbo.get() >= bValue) {
			cstr.setEntailed();
		} else if (nbz.get() > bGap) {
			cstr.fail();
		} else if (nbz.get() == bGap) {
			putAllOne();
		}
	}

	public void awakeOnLeq() throws ContradictionException {
		if (nbz.get() >= bGap) {
			cstr.setEntailed();
		} else if (nbo.get() > bValue) {
			cstr.fail();
		} else if (nbo.get() == bValue) {
			putAllZero();
		}
	}
	
	public void awakeOnNeq() throws ContradictionException {
		if (nbo.get() > bValue || nbz.get() > bGap) {
			cstr.setEntailed();
		} else if (nbo.get() == bValue) {
			if(nbz.get() == bGap - 1) putAllOne();
			else if( nbz.get() == bGap) cstr.fail();
		} else if (nbz.get() == bGap) {
			if(nbo.get() == bValue - 1) putAllZero();
			else if( nbo.get() == bValue) cstr.fail();
		}
	}
	/**
	 * Computes an upper bound estimate of a linear combination of variables.
	 *
	 * @return the new upper bound value
	 */
	public final int computeUbFromScratch() {
		int s = 0;
		for (int i = 0; i < vars.length; i++) {
			s += vars[i].getSup();
		}
		return s;
	}

	/**
	 * Computes a lower bound estimate of a linear combination of variables.
	 *
	 * @return the new lower bound value
	 */
	public final int computeLbFromScratch() {
		int s = 0;
		for (int i = 0; i < vars.length; i++) {
			s += vars[i].getInf();
		}
		return s;
	}

	public Boolean isEntailedEq() {
		final int lb = computeLbFromScratch();
		final int ub = computeUbFromScratch();
		if (lb > bValue || ub < bValue) {
			return Boolean.FALSE;
		} else if (lb == ub && bValue == lb) {
			return Boolean.TRUE;
		} else {
			return null;
		}
	}
	
	public Boolean isEntailedGeq() {
		if( computeLbFromScratch() >= bValue) {
			return Boolean.TRUE;
		} else if (computeUbFromScratch() < bValue) {
			return Boolean.FALSE;
		} else {
			return null;
		}
	}
	
	public Boolean isEntailedLeq() {
		if (computeUbFromScratch() <= bValue) {
			return Boolean.TRUE;
		} else if (computeLbFromScratch() > bValue) {
			return Boolean.FALSE;
		} else {
			return null;
		}
	}
	
	public Boolean isEntailedNeq() {
		final int lb = computeLbFromScratch();
		final int ub = computeUbFromScratch();
		if (lb > bValue || ub < bValue) {
			return Boolean.TRUE;
		} else if (lb == ub && bValue == lb) {
			return Boolean.FALSE;
		} else {
			return null;
		}
	}

}
