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
package choco.kernel.solver.constraints;

import choco.IPretty;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.Var;

import java.util.logging.Logger;

public interface SConstraint<V extends Var> extends Cloneable,IPretty {


	/**
	 * Reference to an object for logging trace statements related to constraints over integers (using the java.util.logging package)
	 */
	public final static Logger LOGGER = ChocoLogging.getEngineLogger();


	/**
	 * <i>Network management:</i>
	 * Get the number of variables involved in the constraint.
     * @return number of variables involved in the constraint
     */

	int getNbVars();

	/**
	 * <i>Network management:</i>
	 * Accessing the ith variable of a constraint.
	 *
	 * @param i index of the variable in the constraint
     * @return the i^th variable involved in the constraint
	 */

	V getVar(int i);

	/**
	 * <i>Network management:</i>
	 * Accessing the ith variable of a constraint.
	 *
	 * @param i index of the variable in the constraint
     * @return the i^th variable involved in the constraint
	 */

	V getVarQuick(int i);
	
	/**
	 * <i>Network management:</i>
	 * Setting (or overwriting)  the ith variable of a constraint.
	 *
	 * @param i index of the variable in the constraint
	 * @param v the variable (may be an IntDomainVar, SetVar, RealVar, ...
	 */
	void setVar(int i, V v);

	/**
	 * <i>Semantic:</i>
	 * Testing if the constraint is satisfied.
	 * Note that all variables involved in the constraint must be
	 * instantiated when this method is called.
     * @return true if the constraint is satisfied
     */

	boolean isSatisfied();

	/**
	 * computes the constraint modelling the counter-opposite condition of this
	 *
	 * @param solver the current solver
     * @return a new constraint (modelling the opposite condition)  @param solver
	 */
	AbstractSConstraint<V> opposite(Solver solver);

	/**
	 * <i>Network management:</i>
	 * Storing that among all listeners linked to the i-th variable of c,
	 * this (the current constraint) is found at index idx.
	 *
	 * @param i   index of the variable in the constraint
	 * @param idx index of the constraint in the among all listeners linked to that variable
	 */

	void setConstraintIndex(int i, int idx);

	/**
	 * <i>Network management:</i>
	 * Among all listeners linked to the idx-th variable of c,
	 * find the index of constraint c.
	 *
	 * @param idx index of the variable in the constraint
     * @return  index of the constraint within the variable network
	 */

	int getConstraintIdx(int idx);


    /**
     * Return the type of constraint, ie the type of variable involved in the constraint
     * @return
     */
    public SConstraintType getConstraintType();

}
