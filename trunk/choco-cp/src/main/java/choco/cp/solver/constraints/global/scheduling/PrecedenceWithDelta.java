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

import choco.kernel.common.util.IntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Enforce x1 + x2 <= y1 + y2
 **/
public class PrecedenceWithDelta extends AbstractLargeIntSConstraint {

	/**
	 * x1 + x2 <= y1 + y2
	 */
	public PrecedenceWithDelta(IntDomainVar x1, IntDomainVar x2, IntDomainVar y1, IntDomainVar y2) {
		super (new IntDomainVar[]{x1,x2,y1,y2});
	}

	public boolean filterFromXToY() throws ContradictionException {
		boolean b = false;
		int lbX = vars[0].getInf() + vars[1].getInf();
		b |= vars[2].updateInf(lbX - vars[3].getSup(),cIndices[2]);
		b |= vars[3].updateInf(lbX - vars[2].getSup(),cIndices[3]);
		return b;
	}

	public boolean filterFromYToX() throws ContradictionException {
		boolean b = false;
		int ubY = vars[2].getSup() + vars[3].getSup();
		b |= vars[0].updateSup(ubY - vars[1].getInf(),cIndices[0]);
		b |= vars[1].updateSup(ubY - vars[0].getInf(),cIndices[1]);
		return b;
	}


	public void awakeOnInf(int varIdx) throws ContradictionException {
	   if (varIdx == 0 || varIdx == 1) {
		   propagate();
	   }
	}

	public void awakeOnSup(int varIdx) throws ContradictionException {
		if (varIdx == 2 || varIdx == 3) {
			propagate();
		}
	}

	public void awakeOnInst(int idx) throws ContradictionException {
		propagate();
	}

	public void awakeOnRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
		// do nothing on removals
	}

	public void propagate() throws ContradictionException {
		boolean fixpoint = true;
		while (fixpoint) {
			fixpoint = false;
			fixpoint |= filterFromXToY();
			fixpoint |= filterFromYToX();
		}
	}

	public Boolean isEntailed() {
		int lb1 = vars[0].getInf() + vars[1].getInf();
		int ub1 = vars[0].getSup() + vars[1].getSup();
		int lb2 = vars[2].getInf() + vars[3].getInf();
		int ub2 = vars[2].getSup() + vars[3].getSup();
		if (ub1 <= lb2) return Boolean.TRUE;
		else if (lb1 > ub2) return Boolean.FALSE;
	    else return null;
	}

	public boolean isSatisfied(int[] tuple) {
		return tuple[0] + tuple[1] <= tuple[2] + tuple[3];
	}
}
