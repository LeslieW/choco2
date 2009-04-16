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
import choco.kernel.solver.propagation.PropagationEngine;
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

	public ContradictionException(Object cause, int type) {
		set(cause,type);
	}

	public void set(Object cause, int type) {
		PropagationEngine pe=null;
		contradictionCause = cause;
        contradictionType = type;
		switch(type){
		case VARIABLE:
			pe = ((Var)cause).getSolver().getPropagationEngine();
			pe.setContradictionCause(cause, VARIABLE);
			break;
		case CONSTRAINT:
			pe = ((SConstraint)cause).getSolver().getPropagationEngine();
			pe.setContradictionCause(cause, CONSTRAINT);
			break;
		case DOMAIN:
			pe = ((Domain)cause).getSolver().getPropagationEngine();
			pe.setContradictionCause(cause, DOMAIN);
			break;
		case SEARCH_LIMIT:
			break;
		default:
			break;
		}
	}

	@Override
	public String toString() {
		return "Exception due to " + contradictionCause;
	}

	public Object getContradictionCause() {
		return contradictionCause;
	}

    public int getContraditionType(){
        return contradictionType;
    }
}

