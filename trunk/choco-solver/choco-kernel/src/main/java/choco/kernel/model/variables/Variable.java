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
package choco.kernel.model.variables;

import java.util.Iterator;
import java.util.logging.Logger;

import choco.IPretty;
import choco.kernel.common.IIndex;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.IConstraintList;
import choco.kernel.model.IFindManager;
import choco.kernel.model.IOptions;
import choco.kernel.model.IVariableArray;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintManager;
import choco.kernel.model.constraints.ExpressionManager;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 17 mars 2008
 * Time: 18:26:04
 * Interface for Model's variable.
 * Define all the methods for a variable to be used
 * on the Model.
 */
public interface Variable extends IConstraintList,IVariableArray, IPretty, IIndex, IFindManager, IOptions, IHook {

	final static Logger LOGGER = ChocoLogging.getEngineLogger();

	String getName();
	
	public VariableType getVariableType();

	@Deprecated
	public Iterator<Constraint> getConstraintIterator();
	//replaced by
	//public Iterator<Constraint> getConstraintIterator(Model m);


	@Deprecated
	public int getNbConstraint();
	//replaced by
	//public int getNbConstraint(Model m);

	VariableManager<?> getVariableManager();
	
	ExpressionManager getExpressionManager();
	
	ConstraintManager<?> getConstraintManager();
	
}

