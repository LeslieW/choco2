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

package choco.kernel.solver.variables;

import choco.IExtensionnable;
import choco.IPretty;
import choco.kernel.common.IIndex;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.memory.structure.PartiallyStoredIntVector;
import choco.kernel.memory.structure.PartiallyStoredVector;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.propagation.event.VarEvent;

import java.util.logging.Logger;


/** History:
 * 2007-12-07 : FR_1873619 CPRU: DomOverDeg+DomOverWDeg
 * */

/**
 * Interface for all implementations of domain variables.
 */
public interface Var extends IPretty, IIndex, IExtensionnable {

	/**
	 * Reference to an object for logging trace statements related to IntDomainVar (using the java.util.logging package)
	 */
	public final static Logger LOGGER = ChocoLogging.getEngineLogger();

	public String getName();
	/**
	 * Returns the number of listeners involving the variable.
	 * @return the numbers of listeners involving the variable
	 */

	public int getNbConstraints();

	/**
	 * Returns the <code>i</code>th constraint. <code>i</code>
	 * should be more than or equal to 0, and less or equal to
	 * the number of constraint minus 1.
	 * @param i number of constraint to be returned
	 * @return the ith constraint
	 */

	public SConstraint getConstraint(int i);


	/**
	 * returns the index of the variable in it i-th constraint
	 *
	 * @param constraintIndex the index of the constraint (among all constraints linked to the variable)
	 * @return the index of the variable (0 if this is the first variable of that constraint)
	 */
	public int getVarIndex(int constraintIndex);


	/**
	 * access the data structure storing constraints involving a given variable
	 *
	 * @return the vector of constraints
	 */
	public PartiallyStoredVector<? extends SConstraint> getConstraintVector();

	/**
	 * access the data structure storing indices associated to constraints involving a given variable
	 *
	 * @return the vector of index
	 */
	public PartiallyStoredIntVector getIndexVector();

	/**
	 * <b>Public user API:</b>
	 * <i>Domains :</i> testing whether a variable is instantiated or not.
	 * @return a boolean giving if a variable is instanciated or not
	 */

	public boolean isInstantiated();

	/**
	 * Adds a new listener for the variable, that is a constraint which
	 * should be informed as soon as the variable domain is modified.
	 * The addition can be dynamic (undone upon backtracking) or not
	 * @param c the constraint to add
	 * @param varIdx index of the variable
	 * @param dynamicAddition dynamical addition or not
	 *  @return the number of the listener
	 */
	public int addConstraint(SConstraint c, int varIdx, boolean dynamicAddition);

	/**
	 * returns the object used by the propagation engine to model a propagation event associated to the variable
	 * (an update to its domain)
	 *
	 * @return the propagation event
	 */
	public VarEvent<? extends Var> getEvent();

	/**
	 * This methods should be used if one want to access the different constraints
	 * currently posted on this variable.
	 * <p/>
	 * Indeed, since indices are not always
	 * consecutive, it is the only simple way to achieve this.
	 *
	 * @return an iterator over all constraints involving this variable
	 */
	public DisposableIterator<SConstraint> getConstraintsIterator();
}
