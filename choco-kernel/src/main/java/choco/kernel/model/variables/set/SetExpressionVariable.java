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
package choco.kernel.model.variables.set;

import choco.kernel.common.util.ChocoUtil;
import choco.kernel.model.variables.*;

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 18 mars 2008
 * Since : Choco 2.0.0
 *
 */
public class SetExpressionVariable extends ComponentVariable implements IntBoundedVariable {


    private int lowB;
    private int uppB;

    public SetExpressionVariable(Object parameters, Operator operator, VariableType type, SetExpressionVariable... variables) {
        super(type, operator,parameters, "", variables);
    }

    public SetExpressionVariable(Object parameters, Operator operator, SetExpressionVariable... variables) {
        this(parameters, operator, VariableType.SET_EXPRESSION, variables);
    }

    public SetExpressionVariable[] getVariables() {
        return (SetExpressionVariable[]) variables;
    }

    public int getLowB() {
        return lowB;
    }

    public void setLowB(int lowB) {
        this.lowB = lowB;
    }

    public int getUppB() {
        return uppB;
    }

    public void setUppB(int uppB) {
        this.uppB = uppB;
    }

    private void computeBounds(){
        lowB = Integer.MAX_VALUE;
        uppB = Integer.MIN_VALUE;
        if (operator.equals(Operator.SUM)||operator.equals(Operator.SCALAR)){
            //TODO: compute value!
        } else {
            if(!operator.equals(Operator.NONE)){
                int[] val = computeByOperator(0, 1);
                lowB=(val[0]<lowB?val[0]:lowB);
                uppB=(val[1]>uppB?val[1]:uppB);
            }
        }
       }


    private int[] computeByOperator(int i, int j){
        int i1 = ((SetExpressionVariable)variables[i]).getLowB();
        int i2 = ((SetExpressionVariable)variables[j]).getLowB();
        int s1 = ((SetExpressionVariable)variables[i]).getUppB();
        int s2 = ((SetExpressionVariable)variables[i]).getUppB();
        int[]vals = new int[4];
        switch (operator) {
            case MINUS:
                vals[0] = i1 - i2;
                vals[1] = i1 - s2;
                vals[2] = s1 - i2;
                vals[3] = s1 - s2;
                break;
            case MULT:
                vals[0] = i1 * i2;
                vals[1] = i1 * s2;
                vals[2] = s1 * i2;
                vals[3] = s1 * s2;
                break;
            case NONE:
                break;
            case PLUS:
                vals[0] = i1 + i2;
                vals[1] = i1 + s2;
                vals[2] = s1 + i2;
                vals[3] = s1 + s2;
                break;
            default:
                vals[0] = Integer.MIN_VALUE;
                vals[1] = Integer.MIN_VALUE;
                vals[2] = Integer.MAX_VALUE;
                vals[3] = Integer.MAX_VALUE;
        }
        int[] bounds = new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE};
        for (int val : vals) {
            bounds[0] = Math.min(bounds[0], val);
            bounds[1] = Math.max(bounds[1], val);
        }
        return bounds;
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
            listVars = ChocoUtil.getNonRedundantObjects(Variable.class, variables);
        }
        return listVars;
    }

}
