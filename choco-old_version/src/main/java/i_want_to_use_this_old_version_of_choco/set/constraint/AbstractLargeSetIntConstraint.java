package i_want_to_use_this_old_version_of_choco.set.constraint;

import i_want_to_use_this_old_version_of_choco.Var;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.IntVar;
import i_want_to_use_this_old_version_of_choco.reified.AbstractReifiedConstraint;
import i_want_to_use_this_old_version_of_choco.set.SetVar;


/**
 * A class to represent a large constraint including both set and int variables in
 * its scope.
 **/
public abstract class AbstractLargeSetIntConstraint extends AbstractMixedConstraint {

	/**
	 * The set variables representing the first part of the scope of the constraint.
	 */
	public SetVar[] svars;

	/**
	 * The int variables representing the rest scope of the constraint.
	 */
	public IntDomainVar[] ivars;


	public int nbvar;

	/**
	 * The index of the constraint among all listeners of its set variable.
	 */
	public int[] set_cIndices;


	/**
	 * The index of the constraint among all listeners of its int variable.
	 */
	public int[] int_cIndices;

	/**
	 * Allocate the data used for both sets of variables
	 * @param nbSetVar
	 * @param nbIntVar
	 */
	public AbstractLargeSetIntConstraint(int nbSetVar, int nbIntVar) {
		super();
		svars = new SetVar[nbSetVar];
		set_cIndices = new int[nbSetVar];
		ivars = new IntDomainVar[nbIntVar];
		int_cIndices = new int[nbIntVar];
		nbvar = nbSetVar + nbIntVar;
	}

	public AbstractLargeSetIntConstraint(IntVar[] intvars, SetVar[] setvars) {
		super();
		ivars = new IntDomainVar[intvars.length];
		System.arraycopy(intvars, 0, ivars, 0, intvars.length);
		int_cIndices = new int[intvars.length];
		svars = new SetVar[setvars.length];
		System.arraycopy(setvars, 0, svars, 0, setvars.length);
		set_cIndices = new int[setvars.length];
		nbvar = intvars.length + setvars.length;
	}

	/**
	 * Let v be the i-th var of c, records that c is the n-th constraint involving v.
	 */
	public void setConstraintIndex(int i, int val) {
		if (i >= 0) {
			if (i < svars.length)
				set_cIndices[i] = val;
			else if (i < nbvar)
				int_cIndices[i - svars.length] = val;
			else throw new Error("bug in SetIntConstraintIndex i:" + i + " this: " + this);
		} else throw new Error("bug in SetIntConstraintIndex i:" + i + " this: " + this);
	}

	/**
	 * Returns the index of the constraint in the specified variable.
	 */
	public int getConstraintIdx(int idx) {
		if (idx >= 0) {
			if (idx < svars.length) {
				return set_cIndices[idx];
			} else if (idx < nbvar) {
				return int_cIndices[idx - svars.length];
			} else return -1;
		} else
			return -1;
	}

	/**
	 * Checks if all the variables are instantiated.
	 */
	public boolean isCompletelyInstantiated() {
		for (SetVar var : svars) {
			if (!var.isInstantiated()) return false;
		}
		for (IntVar var : ivars) {
			if (!var.isInstantiated()) return false;
		}
		return true;
	}

	/**
	 * Returns the number of variables.
	 */
	public int getNbVars() {
		return nbvar;
	}


	public SetVar getSetVar(int i) {
		if (i >= 0 && i < svars.length) {
				return svars[i];
		} else return null;
	}

	public IntDomainVar getIntVar(int i) {
		if (i >= 0 && i < ivars.length) {
				return ivars[i];
		} else return null;
	}

	/**
	 * Gets the specified variable.
	 */
	public Var getVar(int i) {
		if (i >= 0) {
			if (i < svars.length)
				return svars[i];
			else if (i < nbvar)
				return ivars[i - svars.length];
			else return null;
		} else
			return null;
	}


	public void setVar(int i, Var v) {
		if (i >= 0) {
			if (i < svars.length)
				svars[i] = (SetVar) v;
			else if (i < nbvar)
				ivars[i - svars.length] = (IntDomainVar) v;
			else throw new Error("BUG in CSP network management: too large index for set/int Var");
		} else throw new Error("BUG in CSP network management: negative index for set/int Var");
	}


	public int assignIndices(AbstractReifiedConstraint root, int i, boolean dynamicAddition) {
		int j = i;
		for (int k = 0; k < getNbVars(); k++) {
			j++;
			int cidx;
			if (k < svars.length)
				cidx = root.connectVar(svars[k], j, dynamicAddition);
			else
				cidx = root.connectVar(ivars[k - svars.length], j, dynamicAddition);
			setConstraintIndex(k, cidx);
		}
		return j;
	}


}
