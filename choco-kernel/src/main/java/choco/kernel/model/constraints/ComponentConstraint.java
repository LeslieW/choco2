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
package choco.kernel.model.constraints;

import choco.kernel.common.util.ChocoUtil;
import choco.kernel.model.variables.Variable;

import java.util.Iterator;

public class ComponentConstraint extends AbstractConstraint {

    protected Variable[] variables;
    protected Object parameters;


    public ComponentConstraint(final ConstraintType constraintType, final Object parameters, final Variable[] variables) {
        super(constraintType);
        this.variables = variables;
        this.parameters = parameters;
    }

    public ComponentConstraint(final String componentClassName, final Object parameters, final Variable[] variables) {
        super(componentClassName);
        this.variables = variables;
        this.parameters = parameters;
    }

    public ComponentConstraint(final Class componentClass, final Object parameters, final Variable[] variables) {
        super(componentClass.getName());
        this.variables = variables;
        this.parameters = parameters;
    }

    public Object getParameters() {
        return parameters;
    }

    @Override
	public Variable[] getVariables() {
        return variables;
    }

    public final Variable getVariable(final int idx) {
        return variables[idx];
    }
    /**
     * @see choco.kernel.model.constraints.Constraint#getNbVars()
     */
    @Override
    public int getNbVars() {
        return variables.length;
    }


    @Override
	public int[] getFavoriteDomains() {
        if (cm == null) {
            cm = (ConstraintManager)loadManager(getManager());
        }
        return  cm.getFavoriteDomains(options);
    }

    /**
     * @see choco.kernel.model.constraints.Constraint#getVariableIterator()
     */
    @Override
    public Iterator<Variable> getVariableIterator() {
       return ChocoUtil.iterator(extractVariables());
    }
}
