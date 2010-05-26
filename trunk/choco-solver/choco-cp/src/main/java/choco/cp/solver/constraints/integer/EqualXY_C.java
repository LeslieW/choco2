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
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.integer.AbstractBinIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Implements a constraint X + Y == C, with X and Y two variables and C a constant.
 */
public final class EqualXY_C extends AbstractBinIntSConstraint {

	/**
	 * The search constant of the constraint
	 */
	protected final int cste;
	
	private DisposableIntIterator reuseIter;
	
	/**
	 * Constructs the constraint with the specified variables and constant.
	 *
	 * @param x0 first IntDomainVar
	 * @param x1 second IntDomainVar
	 * @param c  The search constant used in the disequality.
	 */

	public EqualXY_C(IntDomainVar x0, IntDomainVar x1, int c) {
		super(x0, x1);
		this.cste = c;
	}

	@Override
	public int getFilteredEventMask(int idx) {
		if(idx == 0){
			if(v0.hasEnumeratedDomain()){
				return IntVarEvent.INSTINT_MASK + IntVarEvent.REMVAL_MASK;
			}else{
				return IntVarEvent.INSTINT_MASK + IntVarEvent.BOUNDS_MASK;
			}
		}else{
			if(v1.hasEnumeratedDomain()){
				return IntVarEvent.INSTINT_MASK + IntVarEvent.REMVAL_MASK;
			}else{
				return IntVarEvent.INSTINT_MASK + IntVarEvent.BOUNDS_MASK;
			}
		}
	}

	
	private final void updateInfV0() throws ContradictionException {
		v0.updateInf(cste - v1.getSup(), this, false);
	}
	
	private final void updateInfV1() throws ContradictionException {
		v1.updateInf(cste - v0.getSup(), this, false);
	}
	
	private final void updateSupV0() throws ContradictionException {
		v0.updateSup(cste - v1.getInf(), this, false);
	}
	
	private final void updateSupV1() throws ContradictionException {
		v1.updateSup(cste - v0.getInf(), this, false);
	}
	/**
	 * The one and only propagation method, using foward checking
	 */

	public void propagate() throws ContradictionException {
		updateInfV0();
		updateSupV0();
		updateInfV1();
		updateSupV1();
		// ensure that, in case of enumerated domains, holes are also propagated
		reuseIter = v0.getDomain().getIterator();
		int val;
		try{
			while (reuseIter.hasNext()) {
				val = reuseIter.next();
				if ( ! v1.canBeInstantiatedTo( cste - val)) {
					v0.removeVal(val, this, false);
				}
			}
		}finally{ 
			reuseIter.dispose();
		}
		reuseIter = v1.getDomain().getIterator();
		try{
			while (reuseIter.hasNext()) {
				val = reuseIter.next();
				if ( ! v0.canBeInstantiatedTo( cste - val)) {
					v1.removeVal(val, this, false);
				}
			}
		}finally{
			reuseIter.dispose();
		}
	}


	@Override
	public final void awakeOnInf(int idx) throws ContradictionException {
		if (idx == 0) updateSupV1();
		else updateSupV0();
	}


	@Override
	public final void awakeOnSup(int idx) throws ContradictionException {
		if (idx == 0) updateInfV1();
		else updateInfV0();
	}

	@Override
	public final void awakeOnInst(int idx) throws ContradictionException {
		if (idx == 0) v1.instantiate(cste - v0.getVal(), this, false);
		else v0.instantiate(cste - v1.getVal(), this, false);
	}


	@Override
	public void awakeOnRem(int idx, int x) throws ContradictionException {
		if (idx == 0) v1.removeVal( cste - x, this, false);
		else v0.removeVal( cste - x, this, false);
	}

	/**
	 * Checks if the listeners must be checked or must fail.
	 */

	@Override
	public final Boolean isEntailed() {
		if ((v0.getSup() + v1.getSup() < cste) ||
				(v0.getInf() + v1.getInf() > cste))
			return Boolean.FALSE;
		else if (v0.isInstantiated() &&
				v1.isInstantiated() &&
				(v0.getVal() == v1.getVal() + cste))
			return Boolean.TRUE;
		else
			return null;
	}

	/**
	 * Checks if the constraint is satisfied when the variables are instantiated.
	 */

	@Override
	public final boolean isSatisfied(int[] tuple) {
		return (tuple[0] + tuple[1] == this.cste);
	}

	/**
	 * tests if the constraint is consistent with respect to the current state of domains
	 *
	 * @return true iff the constraint is bound consistent (weaker than arc consistent)
	 */
	@Override
	public final boolean isConsistent() {
		return ((v0.getInf()  + v1.getSup() == cste) && (v0.getSup() + v1.getInf() == cste));
	}

	@Override
	public final AbstractSConstraint opposite(Solver solver) {
		return (AbstractSConstraint) solver.neq(solver.plus(v0, v1), cste);
	}


	@Override
	public final String pretty() {
		StringBuffer sb = new StringBuffer();
		sb.append(v0.toString());
		sb.append(" + ");
		sb.append(v1.toString());
		sb.append(" = ");
		sb.append(this.cste);
		return sb.toString();
	}

}
