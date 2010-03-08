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


package choco.cp.solver.constraints.integer;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.integer.AbstractBinIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Implements a constraint X + Y !== C, with X and Y two variables and C a constant.
 */
public final class NotEqualXY_C extends AbstractBinIntSConstraint {

	/**
	 * The search constant of the constraint
	 */
	protected final int cste;

	/**
	 * Constructs the constraint with the specified variables and constant.
	 *
	 * @param x0 first IntDomainVar
	 * @param x1 second IntDomainVar
	 * @param c  The search constant used in the disequality.
	 */

	public NotEqualXY_C(IntDomainVar x0, IntDomainVar x1, int c) {
		super(x0, x1);
		this.cste = c;
	}



	@Override
	public int getFilteredEventMask(int idx) {
		//Principle : if v0 is instantiated and v1 is enumerated, then awakeOnInst(0) performs all needed pruning
		//Otherwise, we must check if we can remove the value from v1 when the bounds has changed.
		final IntDomainVar v = idx == 0 ? v1 : v0;
		return v.hasEnumeratedDomain() ? 
				IntVarEvent.INSTINTbitvector : IntVarEvent.INSTINTbitvector + IntVarEvent.BOUNDSbitvector;
	}

	private void removeValV0() throws ContradictionException {
		if(v0.removeVal(cste - v1.getVal(), this, false)){
            this.setEntailed();
        }else if(!v0.canBeInstantiatedTo(cste - v1.getVal())){
            this.setEntailed();
        }
	}

	private void removeValV1() throws ContradictionException {
		if(v1.removeVal(cste - v0.getVal(), this, false)){
            this.setEntailed();
        }else if(!v1.canBeInstantiatedTo(cste - v0.getVal())){
            this.setEntailed();
        }
	}

	/**
	 * The one and only propagation method, using foward checking
	 */

	public final void propagate() throws ContradictionException {
		if (v0.isInstantiated()) removeValV1();
		else if (v1.isInstantiated()) removeValV0();
	}

	@Override
	public void awakeOnInf(int idx) throws ContradictionException {
		propagate();
	}

	@Override
	public void awakeOnSup(int idx) throws ContradictionException {
		propagate();
	}

	@Override
	public final void awakeOnInst(int idx) throws ContradictionException {
		if (idx == 0) removeValV1();
		else{
            assert (idx == 1);
            removeValV0();
        }
	}
	
	

	@Override
	public void awakeOnRem(int varIdx, int val) throws ContradictionException {}



	@Override
	public void awakeOnRemovals(int idx, DisposableIntIterator deltaDomain)
			throws ContradictionException {}



	/**
	 * Checks if the listeners must be checked or must fail.
	 */

	@Override
	public Boolean isEntailed() {
		if ((v0.getSup() + v1.getSup() < cste) ||
				(v1.getInf() + v0.getInf() > cste))
			return Boolean.TRUE;
		else if ( v0.isInstantiated() 
				&& v1.isInstantiated() 
				&& v0.getInf() + v1.getInf() == this.cste)
			return Boolean.FALSE;
		else
			return null;
	}

	/**
	 * Checks if the constraint is satisfied when the variables are instantiated.
	 */

	@Override
	public boolean isSatisfied(int[] tuple) {
		return (tuple[0] + tuple[1] != this.cste);
	}

	/**
	 * tests if the constraint is consistent with respect to the current state of domains
	 *
	 * @return true iff the constraint is bound consistent (weaker than arc consistent)
	 */
	@Override
	public boolean isConsistent() {
		//TODO not really easy to invert from X != Y + C
//		return ((v0.isInstantiated()) ?
//				((v1.hasEnumeratedDomain()) ?
//						(!v1.canBeInstantiatedTo(v0.getVal())) :
//							((v1.getInf() != v0.getVal()) && (v1.getSup() != v0.getVal()))) :
//								((!v1.isInstantiated()) || ((v0.hasEnumeratedDomain()) ?
//										(!v0.canBeInstantiatedTo(v1.getVal())) :
//											((v0.getInf() != v1.getVal()) && (v0.getSup() != v1.getVal())))));
		return false;
	}

	@Override
	public AbstractSConstraint opposite(Solver solver) {
		return (AbstractSConstraint) solver.eq(solver.plus(v0, v1), cste);
	}


	@Override
	public String pretty() {
		StringBuffer sb = new StringBuffer();
		sb.append(v0).append(" + ").append(v1);
		sb.append(" != ").append(StringUtils.pretty(this.cste));
		return sb.toString();
	}

}
