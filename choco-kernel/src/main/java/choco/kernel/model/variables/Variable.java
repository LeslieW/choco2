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

import choco.IPretty;
import choco.kernel.common.IIndex;
import choco.kernel.model.IOptions;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;

import java.util.Iterator;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 17 mars 2008
 * Time: 18:26:04
 * Interface for Model's variable.
 * Define all the methods for a variable to be used
 * on the Model.
 */
public interface Variable extends IPretty, IIndex, IOptions {

	public VariableType getVariableType();

    public void addConstraint(Constraint c);

    public void removeConstraint(Constraint c);

    @Deprecated
    public Iterator<Constraint> getConstraintIterator();
    //replaced by
    public Iterator<Constraint> getConstraintIterator(Model m);


    @Deprecated
    public int getNbConstraint();
    //replaced by
    public int getNbConstraint(Model m);

    /**
     * Extract first level sub-variables of a variable
     * and return an array of non redundant sub-variable.
     * In simple variable case, return a an array
     * with just one element.
     * Really usefull when expression variables.
     * @return a hashset of every sub variables contained in the Variable.
     */
    public Variable[] extractVariables();


    /**
     * Set the class manager
     * @param properties
     */
    public void findManager(Properties properties);

    /**
     * Return wether a variable has been added to a model
     * @param modelIndex
     * @return
     */
    public Boolean alreadyIn(int modelIndex);

    /**
     * Record the adition of the variable to the model
     * @param modelIndex
     */
    public void addModelIndex(int modelIndex);

    /**
     * Remove the adition of the variable to the model
     * @param modelIndex
     */
    public void remModelIndex(int modelIndex);
}
