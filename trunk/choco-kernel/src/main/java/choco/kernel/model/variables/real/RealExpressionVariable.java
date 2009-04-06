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
package choco.kernel.model.variables.real;

import choco.kernel.common.util.ChocoUtil;
import choco.kernel.model.constraints.ConstraintManager;
import choco.kernel.model.variables.*;

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 18 mars 2008
 * Since : Choco 2.0.0
 *
 */
public class RealExpressionVariable extends ComponentVariable implements DoubleBoundedVariable{

	protected double lowB, uppB;
    protected ConstraintManager rcm;

	public RealExpressionVariable(Object parameters, Operator operator, VariableType type, RealExpressionVariable... variables) {
        super(type, operator, parameters, "", variables);

    }

    public RealExpressionVariable(Object parameters, Operator operator, RealExpressionVariable... variables) {
        this(parameters, operator, VariableType.REAL_EXPRESSION, variables);
    }

    public RealExpressionVariable[] getVariables() {
        return (RealExpressionVariable[]) variables;
    }

    public double getUppB() {
        return uppB;
    }

    public void setUppB(double uppB) {
        this.uppB = uppB;
    }

    public double getLowB() {
        return lowB;
    }

    public void setLowB(double lowB) {
        this.lowB = lowB;
    }

    @Override
	public String pretty() {
        //TODO : do pretty
        //return operator==Operator.NONE ? this.pretty(binf, bsup) : super.pretty() ;
        return null;
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


    public ConstraintManager getRcm(){
        if(rcm == null){
            rcm = (ConstraintManager)loadManager(getOperatorClass());
        }
        return rcm;
    }

}
