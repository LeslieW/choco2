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
import choco.kernel.common.util.IntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.integer.AbstractBinIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/*
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 22 avr. 2008
 * Since : Choco 2.0.0
 * enforce the two variables to take the same sign
 * 0 is considered to have both signs
 *
 */
public class SignOp extends AbstractBinIntSConstraint {

	/**
	 * enforce the two variables to take the same sign
     * 0 is considered to have both signs
	 * if same is true and a different sign if same is false
	 */
	protected boolean same;

	/**
	 * @param x0 first IntDomainVar
	 * @param x1 second IntDomainVar
	 * @param same  The search constant used in the disequality.
	 */

	public SignOp(IntDomainVar x0, IntDomainVar x1, boolean same) {
		super(x0, x1);
		this.same = same;
	}

    public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINTbitvector + IntVarEvent.BOUNDSbitvector;
    }

    @Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public void filterSame(IntDomainVar x1, IntDomainVar x2, int idx2) throws ContradictionException {
        if (x1.getSup() < 0) {
			x2.updateSup(0,idx2);
			setEntailed();
		} else if (x1.getInf() > 0) {
			x2.updateInf(0,idx2);
			setEntailed();
		}
	}

	public void filterNotSame(IntDomainVar x1, IntDomainVar x2, int idx1, int idx2) throws ContradictionException {
        if(x1.getInf()==0)x1.updateInf( 1, idx1);
        if(x1.getSup()==0)x1.updateSup(-1, idx1);
		if (x1.getSup() < 0) {
			x2.updateInf(1,idx2);
			setEntailed();
		} else if (x1.getInf() > 0) {
			x2.updateSup(-1,idx2);
			setEntailed();
		}
	}


	public void filter() throws ContradictionException {
		if (same) {
			filterSame(v0,v1,cIdx1);
			filterSame(v1,v0,cIdx0);
		} else {
			filterNotSame(v0,v1,cIdx0, cIdx1);
			filterNotSame(v1,v0,cIdx1, cIdx0);
		}
	}

	/**
	 * The one and only propagation method, using foward checking
	 */
	@Override
	public void propagate() throws ContradictionException {
        if (!same) {
            v0.removeVal(0,cIdx0);
            v1.removeVal(0,cIdx1);
        }
        filter();
	}

	@Override
	public void awakeOnInf(int idx) throws ContradictionException {
		filter();
	}

	@Override
	public void awakeOnSup(int idx) throws ContradictionException {
		filter();
	}

	@Override
	public void awakeOnInst(int idx) throws ContradictionException {
		filter();
	}

	@Override
	public void awakeOnBounds(int varIndex) throws ContradictionException {
		filter();
	}

	@Override
	public void awakeOnRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {

	}

    /**
	 * Checks if the listeners must be checked or must fail.
	 */

	@Override
	public Boolean isEntailed() {
           if (v0.isInstantiatedTo(0) ||
               v1.isInstantiatedTo(0)) {
                return same ? Boolean.TRUE : Boolean.FALSE;
           }
           if (v0.getInf() >= 0 && v1.getInf() >= 0 ||
               v0.getSup() <= 0 && v1.getSup() <= 0) {
               return same ? Boolean.TRUE : Boolean.FALSE;
           }
           else if (v0.getInf() > 0 && v1.getSup() < 0 ||
                    v1.getInf() > 0 && v0.getSup() < 0)
            return same ? Boolean.FALSE : Boolean.TRUE;
        return null;
	}

	/**
	 * Checks if the constraint is satisfied when the variables are instantiated.
	 */

	@Override
	public boolean isSatisfied(int[] tuple) {
		if (!same)
           return (tuple[0] != 0 && tuple[1] != 0) &&
                  (
                          (tuple[0] > 0 && tuple[1] < 0)
                          || (tuple[0] < 0 && tuple[1] > 0)
                  );
	    else
            return (tuple[0] == 0 || tuple[1] == 0) ||
                   (tuple[0] >= 0 && tuple[1] >= 0) ||
                   (tuple[0] <= 0 && tuple[1] <= 0);
    }

	/**
	 * tests if the constraint is consistent with respect to the current state of domains
	 *
	 * @return true iff the constraint is bound consistent (weaker than arc consistent)
	 */
	@Override
	public boolean isConsistent() {
		throw new UnsupportedOperationException("is consistent not implemented on SignOp");
	}

	@Override
	public AbstractSConstraint opposite() {
		return new SignOp(v0,v1,!same);
	}


	@Override
	public String pretty() {
		StringBuffer sb = new StringBuffer();
		sb.append(v0.toString());
		if (same) {
			sb.append(" of same sign than ");
		} else {
			sb.append(" of different sign than ");
		}
		sb.append(v1.toString());
		return sb.toString();
	}

}
