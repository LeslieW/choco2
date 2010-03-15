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


import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.solver.variables.integer.IntDomainVar;
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

	public AbstractLargeSetIntSConstraint(IntDomainVar[] intvars, SetVar[] setvars) {
		super(ArrayUtils.append(setvars, intvars));
		this.ivars= intvars;
		this.svars = setvars;
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

}
