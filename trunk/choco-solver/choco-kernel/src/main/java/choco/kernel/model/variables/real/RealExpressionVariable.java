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

import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.IConstraintList;
import choco.kernel.model.constraints.ConstraintManager;
import choco.kernel.model.constraints.ManagerFactory;
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
   
	
    protected RealExpressionVariable(VariableType variableType,
			boolean enableOption, Object parameters, IConstraintList constraints) {
		super(variableType, enableOption, parameters, constraints);
    }

	public RealExpressionVariable(Object parameters, Operator operator, RealExpressionVariable... variables) {
        super( VariableType.REAL_EXPRESSION, operator, parameters, variables);
    }

    public RealExpressionVariable[] getExpVariables() {
    	//FIXME remove function (deal with only with variable or component-variable ?)
		RealExpressionVariable[] r = new RealExpressionVariable[getNbVars()];
		System.arraycopy(getVariables(), 0, r, 0, getNbVars());
		return r;
    }

    public final double getUppB() {
        return uppB;
    }

    public void setUppB(double uppB) {
        this.uppB = uppB;
    }

    public final double getLowB() {
        return lowB;
    }

    public void setLowB(double lowB) {
        this.lowB = lowB;
    }

    public ConstraintManager<?> getRealConstraintManager(){
    	return ManagerFactory.loadConstraintManager(getOperatorClass());
    }

}
