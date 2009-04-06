// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco;

import i_want_to_use_this_old_version_of_choco.prop.PropagationEngine;

/**
 * An exception thrown when a contradiction achieved.
 */
public class ContradictionException extends Exception {
	/**
	 * An exception may have a local cause (the last variable
	 * / constraint responsible for the failure)
	 */

	/**
	 * Contradiction without any identifiable cause
	 */

	public ContradictionException(AbstractProblem p) {
		PropagationEngine pe = p.getPropagationEngine();
		pe.setNoContradictionCause();
	}

	/**
	 * Constructs a new contradiction with the specified cause.
	 *
	 * @param cause the the last object (variable, constraint) responsible
	 *              for the failure of propagation
	 */

	public ContradictionException(Entity cause) {
		PropagationEngine pe = cause.getProblem().getPropagationEngine();
		pe.setContradictionCause(cause);
	}


	public ContradictionException(AbstractVar v, AbstractConstraint ct) {
		PropagationEngine pe = v.getProblem().getPropagationEngine();
		pe.setContradictionCause(v);
		v.incNbFailure();
		ct.incNbFailure();
	}

	public ContradictionException(AbstractConstraint ct) {
		PropagationEngine pe = ct.getProblem().getPropagationEngine();
		pe.setContradictionCause(ct);
		ct.incNbFailure();
	}

	public ContradictionException(AbstractVar v) {
		PropagationEngine pe = v.getProblem().getPropagationEngine();
		pe.setContradictionCause(v);
		v.incNbFailure();
	}

}

