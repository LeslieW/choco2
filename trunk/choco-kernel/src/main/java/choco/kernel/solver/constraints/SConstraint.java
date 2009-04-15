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

import java.util.logging.Logger;

import choco.IPretty;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.Var;

public interface SConstraint extends Cloneable,IPretty {


	/**
	 * Reference to an object for logging trace statements related to constraints over integers (using the java.util.logging package)
	 */

	public final static Logger LOGGER = ChocoLogging.getPropagationLogger();


	/**
	 * <i>Network management:</i>
	 * Get the number of variables involved in the constraint.
	 */

	int getNbVars();

	/**
	 * <i>Network management:</i>
	 * Accessing the ith variable of a constraint.
	 *
	 * @param i index of the variable in the constraint
	 */

	Var getVar(int i);

	/**
	 * <i>Network management:</i>
	 * Setting (or overwriting)  the ith variable of a constraint.
	 *
	 * @param i index of the variable in the constraint
	 * @param v the variable (may be an IntDomainVar, SetVar, RealVar, ...
	 */
	void setVar(int i, Var v);

	/**
	 * <i>Semantic:</i>
	 * Testing if the constraint is satisfied.
	 * Note that all variables involved in the constraint must be
	 * instantiated when this method is called.
	 */

	boolean isSatisfied();

	/**
	 * computes the constraint modelling the counter-opposite condition of this
	 *
	 * @return a new constraint (modelling the opposite condition)
	 */
	AbstractSConstraint opposite();

	/**
	 * returns a copy of the constraint. This copy is a new object, may be a recursive copy in case
	 * of composite constraints. The original and the copy share the same variables & plugins
	 *
	 * @return
	 */
	// public Constraint copy();
	Object clone() throws CloneNotSupportedException;


	/**
	 * computes the index of the i-th variable in the counter-opposite of the constraint
	 *
	 * @param i the index of the variable in the current constraint (this)
	 * @return the index of the variable in the opposite constraint (this.opposite())
	 */
	int getVarIdxInOpposite(int i);

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
	 */

	int getConstraintIdx(int idx);

	/**
	 * Some global constraint might be able to provide
	 * some fine grained information about the "real" degree of a variables.
	 * For example the global constraint on clauses can give the real number of
	 * clauses on each variable
	 * @param idx index of the variable in the constraint
	 * @return a weight given to the variable by the constraint
	 */
	int getFineDegree(int idx);

	/**
	 * Retrieves the model of the entity.
	 */

	public Solver getSolver();


	public void setSolver(Solver solver);


}
