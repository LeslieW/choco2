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

import choco.kernel.model.variables.set.SetConstantVariable;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.propagation.PropagationEngine;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.AbstractSearchStrategy;
import choco.kernel.solver.variables.Domain;
import choco.kernel.solver.variables.Var;

/**
 * An exception thrown when a contradiction achieved.
 */
public class ContradictionException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1542770449283056616L;

	public static final int UNKNOWN = -1;
	public static final int VARIABLE=1;
	public static final int CONSTRAINT=2;
	public static final int DOMAIN=3;
	public static final int SEARCH_LIMIT =4;

	private Object contradictionCause;
    private int contradictionType;
    private int contradictionMove;

    /**
	 * An exception may have a local cause (the last variable
	 * / constraint responsible for the failure)
	 */

	/**
	 * Constructs a new contradiction with the specified cause.
	 *
	 * @param cause the the last object variable responsible
	 *              for the failure of propagation
	 */

	public ContradictionException() {
		this(null,UNKNOWN);
	}
	
	

	public ContradictionException(Object contradictionCause,
			int contradictionType) {
		super();
		this.contradictionCause = contradictionCause;
		this.contradictionType = contradictionType;
		this.contradictionMove = AbstractGlobalSearchStrategy.UP_BRANCH;
	}



	public final void set(Object cause, int type) {
		contradictionCause = cause;
        contradictionType = type;
		contradictionMove = AbstractGlobalSearchStrategy.UP_BRANCH;
	}
	
	public final void set(Object cause, int type, int move) {
		contradictionCause = cause;
        contradictionType = type;
		contradictionMove = move;
	}

	@Override
	public String toString() {
		return "Exception due to " + contradictionCause;
	}

	public final Object getContradictionCause() {
		return contradictionCause;
	}

    public final int getContraditionType(){
        return contradictionType;
    }

	public final int getContradictionType() {
		return contradictionType;
	}
  
    
}

