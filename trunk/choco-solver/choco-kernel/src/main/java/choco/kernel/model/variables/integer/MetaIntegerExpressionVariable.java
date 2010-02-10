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
package choco.kernel.model.variables.integer;

import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Operator;
import choco.kernel.model.variables.Variable;

import java.util.Arrays;
import java.util.Properties;

/*
 * Created by IntelliJ IDEA.
 * User: GROCHART
 * Date: 5 aout 2008
 * Since : Choco 2.0.0
 *
 */
public class MetaIntegerExpressionVariable extends IntegerExpressionVariable {
  protected Constraint[] constraints;

  public MetaIntegerExpressionVariable(Operator operator, Constraint c, IntegerExpressionVariable... variables) {
    super(null, operator, variables);
    constraints = new Constraint[]{c};
  }

  public Constraint[] getConstraints() {
    return constraints;
  }

    /**
     * Preprocessing that helps the garbage collector.
     */
    @Override
    public void freeMemory() {
        Arrays.fill(constraints, null);
        constraints = null;
        super.freeMemory();
    }

    /**
     * Extract first level sub-variables of a variable
     * and return an array of non redundant sub-variable.
     * In simple variable case, return a an array
     * with just one element.
     * Really usefull when expression variables.
     * @return a hashset of every sub variables contained in the Variable.
     */
    public Variable[] extractVariables() {
        if(listVars == null){
            listVars = variables;
            for(Constraint c : constraints){
                listVars = ArrayUtils.append(listVars, c.extractVariables());
            }
            listVars = ArrayUtils.getNonRedundantObjects(Variable.class, listVars);
        }
        return listVars;
    }

    public final void findManager(Properties propertiesFile) {
        super.findManager(propertiesFile);
        for (int i = 0; i < constraints.length; i++) {
            Constraint constraint = constraints[i];
            constraint.findManager(propertiesFile);
        }
    }

}
