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
import choco.kernel.model.variables.*;

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 18 mars 2008
 * Since : Choco 2.0.0
 *
 */
public class IntegerExpressionVariable extends ComponentVariable implements IntBoundedVariable {

    private int lowB = Integer.MAX_VALUE;
    private int uppB = Integer.MIN_VALUE;


    public IntegerExpressionVariable(Object parameters, Operator operator, VariableType type, IntegerExpressionVariable... variables) {
		super(type, operator,parameters, "", variables);
//        init();
	}

    public IntegerExpressionVariable(Object parameters, String operator, VariableType type, IntegerExpressionVariable... variables) {
		super(type, operator,parameters, "", variables);
//        init();
	}

    public IntegerExpressionVariable(Object parameters, Class operator, VariableType type, IntegerExpressionVariable... variables) {
		this(parameters, operator.getName(), type, variables);
	}

    public IntegerExpressionVariable(Object parameters, Operator operator, IntegerExpressionVariable... variables) {
		this(parameters, operator, VariableType.INTEGER_EXPRESSION, variables);
	}

    public IntegerExpressionVariable(Object parameters, String operator, IntegerExpressionVariable... variables) {
		super(VariableType.INTEGER_EXPRESSION, operator,parameters, "", variables);
//        init();
	}

    public IntegerExpressionVariable(Object parameters, Class operator, IntegerExpressionVariable... variables) {
		this(parameters, operator.getName(), variables);
	}

    private void init(){
        if(operator!=null){
            computeBounds();
        }
    }

    public IntegerExpressionVariable[] getVariables() {
		return (IntegerExpressionVariable[]) variables;
	}

	public void setVariables(IntegerExpressionVariable[] variables) {
		this.variables = variables;
	}

    public int getLowB() {
        if(lowB == Integer.MAX_VALUE)init();
        return lowB;
    }

    public void setLowB(int lowB) {
        this.lowB=lowB;
    }

    public int getUppB() {
        if(uppB == Integer.MIN_VALUE)init();
        return uppB;
    }

    public void setUppB(int uppB) {
        this.uppB = uppB;
    }

    private void computeBounds(){
		lowB = Integer.MAX_VALUE;
		uppB = Integer.MIN_VALUE;
        int[] val;
        switch(operator.parameters){
            case 0:
                val = computeByNOperator();
                lowB=(val[0]<lowB?val[0]:lowB);
				uppB=(val[1]>uppB?val[1]:uppB);
                break;
            case 1:
                //for ABS and NEG
                val = computeByOperator(0);
				lowB=(val[0]<lowB?val[0]:lowB);
				uppB=(val[1]>uppB?val[1]:uppB);
                break;
            case 2:
                //for others
                val = computeByOperator(0, 1);
				lowB=(val[0]<lowB?val[0]:lowB);
				uppB=(val[1]>uppB?val[1]:uppB);
                break;
            default:
                break;
        }
	}

    private int[] computeByOperator(int i) {
        int i1 = ((IntegerExpressionVariable) variables[i]).getLowB();
        int s1 = ((IntegerExpressionVariable) variables[i]).getUppB();
        int[] vals = new int[2];
        switch (operator) {
            case ABS:
                vals[0] = Math.min(Math.abs(i1), Math.abs(s1));
                if(i1<0 && s1>0)vals[0] = 0;
                vals[1] = Math.max(Math.abs(i1), Math.abs(s1));
                break;
            case NEG:
                vals[0] = -s1;
                vals[1] = -i1;
                break;
            case NONE:
                break;
            default:
                vals[0] = Integer.MIN_VALUE;
                vals[1] = Integer.MAX_VALUE;
        }
        return vals;
    }

    private int[] computeByOperator(int i, int j) {
        int i1 = ((IntegerExpressionVariable) variables[i]).getLowB();
        int i2 = ((IntegerExpressionVariable) variables[j]).getLowB();
        int s1 = ((IntegerExpressionVariable) variables[i]).getUppB();
        int s2 = ((IntegerExpressionVariable) variables[j]).getUppB();
        int[] vals = new int[4];
        switch (operator) {
            case MINUS:
                vals[0] = i1 - i2;
                vals[1] = i1 - s2;
                vals[2] = s1 - i2;
                vals[3] = s1 - s2;
                break;
            case MOD:
                if(i2==0 && s2==0){
                    vals[0] = Integer.MIN_VALUE;
                    vals[1] = Integer.MIN_VALUE;
                    vals[2] = Integer.MAX_VALUE;
                    vals[3] = Integer.MAX_VALUE;
                    break;
                }
                if(s2==0)s2=1;
                if(i2==0)i2=1;
                vals[0] = i1 % i2;
                vals[1] = i1 % s2;
                vals[2] = s1 % i2;
                vals[3] = s1 % s2;
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
            case POWER:
                vals[0] = (int)Math.pow(i1, i2);
                vals[1] = (int)Math.pow(i1, s2);
                vals[2] = (int)Math.pow(s1, i2);
                vals[3] = (int)Math.pow(s1, s2);
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

    private int[] computeByNOperator() {
        int i1 = 0;
        int s1 = 0;
        int i = 0;
        int[] vals = new int[4];
        switch(operator){
            case SCALAR:
                int cste = variables.length/2;
                do{
                int val2 = ((IntegerConstantVariable) variables[i]).getValue();
                int i2 = ((IntegerExpressionVariable) variables[i+cste]).getLowB()*val2;
                int s2 = ((IntegerExpressionVariable) variables[i+cste]).getUppB()*val2;
                vals[0] = i1 + i2;
                vals[1] = i1 + s2;
                vals[2] = s1 + i2;
                vals[3] = s1 + s2;
                i1 = Integer.MAX_VALUE;
                s1 = Integer.MIN_VALUE;
                    for (int val : vals) {
                        i1 = Math.min(i1, val);
                        s1 = Math.max(s1, val);
                    }
                i++;
            }while(i < cste);
                break;
            case SUM:
                do{
                int i2 = ((IntegerExpressionVariable) variables[i]).getLowB();
                int s2 = ((IntegerExpressionVariable) variables[i]).getUppB();
                vals[0] = i1 + i2;
                vals[1] = i1 + s2;
                vals[2] = s1 + i2;
                vals[3] = s1 + s2;
                i1 = Integer.MAX_VALUE;
                s1 = Integer.MIN_VALUE;
                    for (int val : vals) {
                        i1 = Math.min(i1, val);
                        s1 = Math.max(s1, val);
                    }
                i++;
            }while(i < variables.length);
                break;
            case MAX:
                i1 = Integer.MIN_VALUE;
                s1 = Integer.MIN_VALUE;
                for (ComponentVariable variable : variables) {
                    int i2 = ((IntegerExpressionVariable) variable).getLowB();
                    int s2 = ((IntegerExpressionVariable) variable).getUppB();
                    i1 = Math.max(i1, i2);
                    s1 = Math.max(s1, s2);
                }
                break;
            case MIN:
                i1 = Integer.MAX_VALUE;
                s1 = Integer.MAX_VALUE;
                for (ComponentVariable variable : variables) {
                    int i2 = ((IntegerExpressionVariable) variable).getLowB();
                    int s2 = ((IntegerExpressionVariable) variable).getUppB();
                    i1 = Math.min(i1, i2);
                    s1 = Math.min(s1, s2);
                }
                break;
            default:
                i1 = Integer.MIN_VALUE;
                s1 = Integer.MAX_VALUE;
                break;
        }
        return new int[]{i1, s1};
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
            listVars = ArrayUtils.getNonRedundantObjects(Variable.class, variables);
        }
        return listVars;
    }
}
