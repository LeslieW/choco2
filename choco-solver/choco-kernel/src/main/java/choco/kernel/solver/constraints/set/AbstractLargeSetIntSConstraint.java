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
package choco.kernel.solver.constraints.set;


import choco.kernel.solver.SolverException;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.integer.IntVar;
import choco.kernel.solver.variables.set.SetVar;


/**
 * A class to represent a large constraint including both set and int variables in
 * its scope.
 **/
public abstract class AbstractLargeSetIntSConstraint extends AbstractMixedSetIntSConstraint {

	/**
	 * The set variables representing the first part of the scope of the constraint.
	 */
	public SetVar[] svars;

	/**
	 * The int variables representing the rest scope of the constraint.
	 */
	public IntDomainVar[] ivars;


	public int nbvars;

	/**
	 * The index of the constraint among all listeners of its set variable.
	 */
	public int[] set_cIndices;


	/**
	 * The index of the constraint among all listeners of its int variable.
	 */
	public int[] int_cIndices;

	public AbstractLargeSetIntSConstraint(IntVar[] intvars, SetVar[] setvars) {
		super(new Var[]{});
		ivars = new IntDomainVar[intvars.length];
		System.arraycopy(intvars, 0, ivars, 0, intvars.length);
		int_cIndices = new int[intvars.length];
		svars = new SetVar[setvars.length];
		System.arraycopy(setvars, 0, svars, 0, setvars.length);
		set_cIndices = new int[setvars.length];
		nbvars = intvars.length + setvars.length;
	}

	/**
	 * Let v be the i-th var of c, records that c is the n-th constraint involving v.
	 */
	public void setConstraintIndex(int i, int val) {
		if (i >= 0) {
			if (i < svars.length) {
				set_cIndices[i] = val;
			} else if (i < nbvars) {
				int_cIndices[i - svars.length] = val;
			} else {
				throw new SolverException("bug in SetIntConstraintIndex i:" + i + " this: " + this);
			}
		} else {
			throw new SolverException("bug in SetIntConstraintIndex i:" + i + " this: " + this);
		}
	}

	/**
	 * Returns the index of the constraint in the specified variable.
	 */
	public int getConstraintIdx(int idx) {
		if (idx >= 0) {
			if (idx < svars.length) {
				return set_cIndices[idx];
			} else if (idx < nbvars) {
				return int_cIndices[idx - svars.length];
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}

	/**
	 * Checks if all the variables are instantiated.
	 */
	@Override
	public boolean isCompletelyInstantiated() {
		for (SetVar var : svars) {
			if (!var.isInstantiated()) {
				return false;
			}
		}
		for (IntVar var : ivars) {
			if (!var.isInstantiated()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the number of variables.
	 */
	public final int getNbVars() {
		return nbvars;
	}

	public final int getNbSetVars() {
		return svars.length;
	}
	
	public final int getNbIntVars() {
		return ivars.length;
	}
	
	/**
	 * @return the relative index of an integer variable
	 */
	protected int getIntVarIndex (int i) {
	    return i - svars.length;
	}

	public final boolean isSetVarIndex(int i) {
		return i < svars.length;
	}
	
	public final boolean isIntVarIndex(int i) {
		return i >= svars.length;
	}

	/**
	 * Gets the specified variable.
	 */
    @Override
	public Var getVar(int i) {
		if (i >= 0) {
			if (i < svars.length) {
				return svars[i];
			} else if (i < nbvars) {
				return ivars[i - svars.length];
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

    @Override
	public void setVar(int i, Var v) {
		if (i >= 0) {
			if (i < svars.length) {
				svars[i] = (SetVar) v;
			} else if (i < nbvars) {
				ivars[i - svars.length] = (IntDomainVar) v;
			} else {
				throw new SolverException("BUG in CSP network management: too large index for set/int Var");
			}
		} else {
			throw new SolverException("BUG in CSP network management: negative index for set/int Var");
		}
	}
}
