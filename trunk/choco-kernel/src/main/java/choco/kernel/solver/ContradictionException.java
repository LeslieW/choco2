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

import choco.kernel.solver.search.AbstractGlobalSearchStrategy;

/**
 * An exception thrown when a contradiction achieved.
 */
public class ContradictionException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1542770449283056616L;

    public enum Type{
        UNKNOWN(-1), VARIABLE(1), CONSTRAINT(2), DOMAIN(3), SEARCH_LIMIT(4);
        final int value;

        Type(int value) {
            this.value = value;
        }
    }

	/*public static final int UNKNOWN = -1;
	public static final int VARIABLE=1;
	public static final int CONSTRAINT=2;
	public static final int DOMAIN=3;
	public static final int SEARCH_LIMIT =4;*/

	private Object contradictionCause;
    private Type contradictionType;
    private int contradictionMove;

    private Object domOverDegContradictionCause;

    /**
	 * An exception may have a local cause (the last variable
	 * / constraint responsible for the failure)
	 */

	/**
	 * Constructs a new contradiction with the specified cause.
	 *
	 */
	public ContradictionException() {
		this(null,Type.UNKNOWN);
	}
	
	
    /**
	 * Constructs a new contradiction with the specified cause.
	 *
	 * @param contradictionCause the the last object variable responsible
	 *              for the failure of propagation
     * @param contradictionType type of contradiction
	 */
	public ContradictionException(Object contradictionCause,
			Type contradictionType) {
		super();
		this.contradictionCause = contradictionCause;
		this.contradictionType = contradictionType;
		this.contradictionMove = AbstractGlobalSearchStrategy.UP_BRANCH;
        domOverDegContradictionCause = null;
	}



	public final void set(Object cause, Type type) {
		contradictionCause = cause;
        contradictionType = type;
        domOverDegContradictionCause = null;
		contradictionMove = AbstractGlobalSearchStrategy.UP_BRANCH;
	}

    public final void set(Object cause, Type type, Object dCause) {
		contradictionCause = cause;
        contradictionType = type;
        domOverDegContradictionCause = dCause;
		contradictionMove = AbstractGlobalSearchStrategy.UP_BRANCH;
	}

	public final void set(Object cause, Type type, int move) {
		contradictionCause = cause;
        contradictionType = type;
		contradictionMove = move;
        domOverDegContradictionCause = null;
	}

	@Override
	public String toString() {
		return "Exception due to " + contradictionCause;
	}

	public final Object getContradictionCause() {
		return contradictionCause;
	}

    public final Object getDomOverDegContradictionCause(){
        return domOverDegContradictionCause;
    }

    public final int getContradictionMove(){
        return contradictionMove;
    }

    public final boolean isSearchLimitCause(){
        return Type.SEARCH_LIMIT.equals(contradictionType);
    }
     
    
}

