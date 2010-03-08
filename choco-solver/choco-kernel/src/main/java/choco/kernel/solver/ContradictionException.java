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
//*  CHOCO: an open-source Constraint Programming  *
//*     System for Research and Education          *
//*                                                *
//*    contributors listed in choco.Entity.java    *
//*           Copyright (C) F. Laburthe, 1999-2006 *
//**************************************************

package choco.kernel.solver;

import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.limit.AbstractGlobalSearchLimit;

/**
 * An exception thrown when a contradiction achieved.
 */
public class ContradictionException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1542770449283056616L;

	private Object contradictionCause;
    private int contradictionMove;


    /**
	 * Constructs a new contradiction with the specified cause.
	 *
	 * @param contradictionCause the the last object variable responsible
	 *              for the failure of propagation
     */
	protected ContradictionException(Object contradictionCause) {
		super();
		this.contradictionCause = contradictionCause;
		this.contradictionMove = AbstractGlobalSearchStrategy.UP_BRANCH;
    }



	public final void set(Object cause) {
		contradictionCause = cause;
        contradictionMove = AbstractGlobalSearchStrategy.UP_BRANCH;
	}

	public final void set(Object cause, int move) {
		contradictionCause = cause;
		contradictionMove = move;
    }

	@Override
	public String toString() {
		return "Exception due to " + contradictionCause;
	}

	public final Object getContradictionCause() {
		return contradictionCause;
	}

    public final SConstraint getDomOverDegContradictionCause(){
        if(contradictionCause instanceof SConstraint){
            return (SConstraint)contradictionCause;
        }
        return null;
    }

    public final int getContradictionMove(){
        return contradictionMove;
    }

	public final boolean isSearchLimitCause(){
        return contradictionCause instanceof AbstractGlobalSearchLimit;
    }
     
    
}

