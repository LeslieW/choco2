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

import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.variables.Variable;

import java.util.ArrayList;
import java.util.Properties;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 20 janv. 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class ComponentConstraintWithSubConstraints extends ComponentConstraint{

    private final ArrayList<Constraint> constraints;
    private final ArrayList<Variable> listVars;

    public ComponentConstraintWithSubConstraints(ConstraintType constraintType, Variable[] variables,
                                                 Object params, Constraint... constraints) {
        super(constraintType, params, variables);
        this.constraints = new ArrayList<Constraint>(ArrayUtils.toList(constraints));
        this.listVars = new ArrayList<Variable>(ArrayUtils.toList(variables));
    }

    public ComponentConstraintWithSubConstraints(String componentClassName, Variable[] variables,
                                                 Object params, Constraint... constraints) {
        super(componentClassName, appendParameters(params, constraints), variables);
        this.constraints = new ArrayList<Constraint>(ArrayUtils.toList(constraints));
        this.listVars = new ArrayList<Variable>(ArrayUtils.toList(variables));
    }

    public ComponentConstraintWithSubConstraints(Class componentClass, Variable[] variables,
                                                 Object params, Constraint... constraints) {
        super(componentClass, appendParameters(params, constraints), variables);
        this.constraints = new ArrayList<Constraint>(ArrayUtils.toList(constraints));
        this.listVars = new ArrayList<Variable>(ArrayUtils.toList(variables));
    }

    public <V extends Variable> void addElements(V[] vars, Constraint[] cstrs){
        recordIndexes(vars);
        constraints.addAll(ArrayUtils.toList(cstrs));
    }



    private <V extends Variable> void recordIndexes(V[] vars){
        for (V var : vars) {
            if (!listVars.contains(var)) {
                listVars.add(var);
                variables = ArrayUtils.append(variables, new Variable[]{var});
            }
        }
    }


    @Override
    public Variable[] getVariables() {
        return ArrayUtils.toArray(Variable.class, listVars);
    }

    /**
     * @see choco.kernel.model.constraints.Constraint#getNbVars()
     */
    @Override
    public int getNbVars() {
        return listVars.size();
    }

    @Override
    public Object getParameters() {
        return appendParameters(parameters, ArrayUtils.toArray(Constraint.class, constraints));
    }

    private static Object appendParameters(Object parameters, Constraint... constraints){
        return new Object[]{parameters, constraints};
    }


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
    public Variable[] extractVariables() {
        Variable[] listVars = this.getVariables();
        for (Constraint c : constraints) {
            listVars = ArrayUtils.append(listVars, c.extractVariables());
        }
        return ArrayUtils.getNonRedundantObjects(Variable.class, listVars);
    }
}
