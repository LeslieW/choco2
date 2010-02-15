/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2008      *
 **************************************************/
package choco.kernel.model.constraints;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.variables.Variable;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 20 janv. 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class ComponentConstraintWithSubConstraints extends ComponentConstraint {

    private final List<Constraint> constraints;
   
    public ComponentConstraintWithSubConstraints(ConstraintType constraintType, Variable[] variables,
                                                 Object params, Constraint... constraints) {
        super(constraintType, params, variables);
        this.constraints = new LinkedList<Constraint>(ArrayUtils.toList(constraints));
    }

    public ComponentConstraintWithSubConstraints(String componentClassName, Variable[] variables,
                                                 Object params, Constraint... constraints) {
        super(componentClassName, appendParameters(params, constraints), variables);
        this.constraints = new LinkedList<Constraint>(ArrayUtils.toList(constraints));
    }

    public ComponentConstraintWithSubConstraints(Class componentClass, Variable[] variables,
                                                 Object params, Constraint... constraints) {
        super(componentClass, appendParameters(params, constraints), variables);
        this.constraints = new LinkedList<Constraint>(ArrayUtils.toList(constraints));
    }


    public void addElements(Variable[] vars, Constraint... cstrs){
    	Variable[] currentV = getVariables();
    	List<Variable> newV = new LinkedList<Variable>();
    	for (Variable var : vars) {
            	if( ! ArrayUtils.contains(currentV, var))  newV.add(var);
    	}
    	if( ! newV.isEmpty() ) setVariables(ArrayUtils.append(currentV, newV.toArray(new Variable[newV.size()])));
    	constraints.addAll(ArrayUtils.toList(cstrs));
    }

  
    @Override
    public Object getParameters() {
        return appendParameters(parameters, ArrayUtils.toArray(Constraint.class, constraints));
    }

    private static Object appendParameters(Object parameters, Constraint... constraints){
        return new Object[]{parameters, constraints};
    }


    @Override
	public final void findManager(Properties propertiesFile) {
        super.findManager(propertiesFile);
        for (Constraint constraint : constraints) {
            constraint.findManager(propertiesFile);
        }
    }


    /**
     * Extract variables of a constraint
     * and return an array of variables.
     * @return an array of every variables contained in the Constraint.
     */
    @Override
	public Variable[] doExtractVariables() {
        Variable[] listVars = this.getVariables();
        for (Constraint c : constraints) {
            listVars = ArrayUtils.append(listVars, c.extractVariables());
        }
        return ArrayUtils.getNonRedundantObjects(Variable.class, listVars);
    }
}
