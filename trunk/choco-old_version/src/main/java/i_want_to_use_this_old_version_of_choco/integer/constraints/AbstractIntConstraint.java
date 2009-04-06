// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.integer.constraints;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntConstraint;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

import java.util.logging.Logger;

/**
 * An abstract class for all implementations of listeners over search variables.
 */
public abstract class AbstractIntConstraint extends AbstractConstraint implements IntConstraint {

	/**
	 * Reference to an object for logging trace statements related to constraints over integers (using the java.util.logging package)
	 */

	protected static Logger logger = Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.prop.const");

	/**
	 * Default propagation on improved lower bound: propagation on domain revision.
	 */

	public void awakeOnInf(int idx) throws ContradictionException {
		awakeOnVar(idx);
	}


	/**
	 * Default propagation on improved upper bound: propagation on domain revision.
	 */

	public void awakeOnSup(int idx) throws ContradictionException {
		awakeOnVar(idx);
	}


	/**
	 * Default propagation on instantiation: full constraint re-propagation.
	 */

	public void awakeOnInst(int idx) throws ContradictionException {
		propagate();
	}


	/**
	 * Default propagation on one value removal: propagation on domain revision.
	 */

	public void awakeOnRem(int idx, int x) throws ContradictionException {
		awakeOnVar(idx);
	}


	/**
	 * The default implementation of propagation when a variable has been modified
	 * consists in iterating all values that have been removed (the delta domain)
	 * and propagate them one after another, incrementally.
	 *
	 * @param idx
	 * @throws i_want_to_use_this_old_version_of_choco.ContradictionException
	 */
	public void awakeOnRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
		if (deltaDomain != null) {
			for (; deltaDomain.hasNext();) {
				int val = deltaDomain.next();
				awakeOnRem(idx, val);
			}
		} else {
			awakeOnVar(idx);
		}
	}

	/**
	 * Checks if all the variables are instantiated.
	 */

	public boolean isCompletelyInstantiated() {
		int n = getNbVars();
		for (int i = 0; i < n; i++) {
			if (!(getIntVar(i).isInstantiated()))
				return false;
		}
		return true;
	}

	public void awakeOnBounds(int varIndex) throws ContradictionException {
		awakeOnInf(varIndex);
		awakeOnSup(varIndex);
	}

	/**
	 * tests if the constraint is consistent with respect to the current state of domains
	 *
	 * @return true if the constraint is entailed (default approximate definition)
	 */
	public boolean isConsistent() {
		return (isEntailed() == Boolean.TRUE);
	}

	/**
	 * returns the (global) index of the constraint among all constraints of the problem
	 * <p/>
	 * This method is dangerous since the introduction of dynamic constraint post.
	 *
	 * @deprecated
	 */
	public int getSelfIndex() {
		AbstractProblem pb = getProblem();
		for (int i = 0; i < pb.getNbIntConstraints(); i++) {
			Constraint c = pb.getIntConstraint(i);
			if (c == this) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Default implementation of the isSatisfied by
	 * delegating to the isSatisfied(int[] tuple)
	 * @return
	 */
	public boolean isSatisfied() {
		int[] tuple = new int[getNbVars()];
		for (int i = 0; i < tuple.length; i++) {
			assert(getIntVar(i).isInstantiated());
			tuple[i] = getIntVar(i).getVal();
		}
		return isSatisfied(tuple);
	}

	/**
	 * TEMPORARY: if not overriden by the constraint, throws an error
	 * to avoid bug using reified constraints in constraints
	 * that have not been changed to fulfill this api yet !
	 * @param tuple
	 * @return
	 */
	public boolean isSatisfied(int[] tuple) {
		throw new Error(this + " needs to implement isSatisfied(int[] tuple) to be embedded in reified constraints");
	}

}



