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

import choco.kernel.model.variables.Variable;

public class ComponentConstraint extends AbstractConstraint {

	protected final Object parameters;

    public ComponentConstraint(final ConstraintType constraintType, final Object parameters, final Variable[] variables) {
        super(constraintType, variables);
        this.parameters = parameters;
    }

    public ComponentConstraint(final String componentClassName, final Object parameters, final Variable[] variables) {
        super(componentClassName, variables);
        this.parameters = parameters;
    }

    public ComponentConstraint(final Class componentClass, final Object parameters, final Variable[] variables) {
        super(componentClass.getName(), variables);
        this.parameters = parameters;
    }
   

    public Object getParameters() {
        return parameters;
    } 

    @Override
	public int[] getFavoriteDomains() {
        return ManagerFactory.loadConstraintManager(getManager()).getFavoriteDomains(getOptions());
    }


}
