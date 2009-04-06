package i_want_to_use_this_old_version_of_choco.set.constraint;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntConstraint;
import i_want_to_use_this_old_version_of_choco.set.SetConstraint;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/**
 * A class for mixed set and int constraint. It refers to both interface
 * SetConstraint and IntConstraint and implements a default behaviour
 * for all events awakeOn...
 */
public abstract class AbstractMixedConstraint extends AbstractConstraint implements SetConstraint, IntConstraint {

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

	public void awakeOnBounds(int varIndex) throws ContradictionException {
		awakeOnInf(varIndex);
		awakeOnSup(varIndex);
	}


	public void awakeOnKer(int varIdx, int x) throws ContradictionException {
		propagate();
	}

	public void awakeOnEnv(int varIdx, int x) throws ContradictionException {
		propagate();
	}

	public void awakeOnInst(int varIdx) throws ContradictionException {
		propagate();
	}

	public void awakeOnEnvRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
		if (deltaDomain != null) {
			for (; deltaDomain.hasNext();) {
				int val = deltaDomain.next();
				awakeOnEnv(idx, val);
			}
		} else
			throw new Error("deltaDomain should not be null in awakeOnEnvRemovals");
	}

	public void awakeOnkerAdditions(int idx, IntIterator deltaDomain) throws ContradictionException {
		if (deltaDomain != null) {
			for (; deltaDomain.hasNext();) {
				int val = deltaDomain.next();
				awakeOnKer(idx, val);
			}
		} else
			throw new Error("deltaDomain should not be null in awakeOnKerAdditions");
	}

	public boolean isCompletelyInstantiated() {
		int n = getNbVars();
		for (int i = 0; i < n; i++) {
			if (!(getVar(i).isInstantiated()))
				return false;
		}
		return true;
	}

	/**
	 * returns the (global) index of the constraint among all constraints of the problem
	 */
	public int getSelfIndex() {
		AbstractProblem pb = getProblem();
		for (int i = 0; i < pb.getNbIntConstraints(); i++) {
			Constraint c = null;
			//c = pb.getSetConstraint(i);  //TODO
			if (c == this) {
				return i;
			}
		}
		return -1;
	}

	public boolean isSatisfied(int[] tuple) {
		throw new Error(this + " needs to implement isSatisfied(int[] tuple) to be embedded in reified constraints");
	}

}
