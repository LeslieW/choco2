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
package choco.cp.solver.constraints.global.scheduling;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 *  Let b be a boolean variables; x0, x1 be two integer variables and k1, k2 two integers.
 * This constraint enforce x0 before x1 if b is true or x1 before x0 if b is false.
 * b0 = 1 <=> x0 + d0 <= x1
 * b0 = 0 <=> x1 + d1 <= x0
 * */
public class VariablePrecedenceDisjoint extends AbstractPrecedenceConstraint {

	private final static int[] FILTERED_EVENT_MASKS = makeMasksArray(5);
	
    public VariablePrecedenceDisjoint(IntDomainVar b, IntDomainVar s0, IntDomainVar d0,
                                      IntDomainVar s1, IntDomainVar d1) {
        super(new IntDomainVar[]{b,s0,d0,s1,d1});
    }

    @Override
    public int getFilteredEventMask(int idx) {
       return FILTERED_EVENT_MASKS[idx];
    }

    
    // propagate x0 + d0 <= x1 (b0 = 1)
	@Override
	public void propagateP1() throws ContradictionException {
		boolean b = true;
        while(b) {
            b = false;
            b |= vars[3].updateInf(vars[1].getInf() + vars[2].getInf(), cIndices[3]);
            b |= vars[1].updateSup(vars[3].getSup() - vars[2].getInf(), cIndices[1]);
            b |= vars[2].updateSup(vars[3].getSup() - vars[1].getInf(), cIndices[2]);
        }
    }

    // propagate x1 + d1 <= x0 (b0 = 0)
    @Override
	public void propagateP2() throws ContradictionException {
		boolean b = true;
        while(b) {
            b = false;
            vars[1].updateInf(vars[3].getInf() + vars[4].getInf(), cIndices[1]);
            vars[3].updateSup(vars[1].getSup() - vars[4].getInf(), cIndices[3]);
            vars[4].updateSup(vars[1].getSup() - vars[3].getInf(), cIndices[4]);
        }
    }

	@Override
	public Boolean isP1Entailed() {
		if (vars[1].getSup() + vars[2].getSup() <= vars[3].getInf())
			return Boolean.TRUE;
		if (vars[1].getInf() + vars[2].getInf() > vars[3].getSup())
			return Boolean.FALSE;
		return null;
	}

	@Override
	public Boolean isP2Entailed() {
		if (vars[3].getSup() + vars[4].getSup() <= vars[1].getInf())
			return Boolean.TRUE;
		if (vars[3].getInf() + vars[4].getInf() > vars[1].getSup())
			return Boolean.FALSE;
		return null;
	}


	@Override
	public boolean isSatisfied() {
		if (vars[BIDX].isInstantiatedTo(1))
			return vars[1].getVal() + vars[2].getVal() <= vars[3].getVal();
		else return vars[3].getVal() + vars[4].getVal() <= vars[1].getVal();
	}

	@Override
	public String pretty() {
		return "VDisjunction " + vars[1] +","+ vars[2]+ " - " + vars[3] + "," + vars[4];
	}

	
}
