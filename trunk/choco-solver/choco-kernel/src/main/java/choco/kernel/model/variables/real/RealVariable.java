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

import java.util.Iterator;

import choco.kernel.common.util.tools.IteratorUtils;
import choco.kernel.model.IConstraintList;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Operator;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.VariableType;
import choco.kernel.model.variables.set.SetVariable;

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 17 mars 2008
 * Since : Choco 2.0.0
 *
 */
public class RealVariable extends RealExpressionVariable {

	
	protected RealVariable(VariableType variableType, boolean enableOption,
			IConstraintList constraints,double lowB, double uppB) {
		super(variableType, enableOption, new double[]{lowB, uppB}, constraints);
		this.lowB = lowB;
		this.uppB = uppB;
		setVariables(this);
	}


	public RealVariable(String name, double lowB, double uppB) {
        //noinspection NullArgumentToVariableArgMethod
        this(VariableType.REAL, true, new ConstraintsDataStructure(), lowB, uppB);
		this.setName(name);
	}



    /**
     * pretty printing of the object. This String is not constant and may depend on the context.
     *
     * @return a readable string representation of the object
     */
    @Override
    public String pretty() {
        return name+" ["+getLowB()+", "+getUppB()+"]";
    }

   
    
    /**
     * Extract first level sub-variables of a variable
     * and return an array of non redundant sub-variable.
     * In simple variable case, return a an array
     * with just one element.
     * Really usefull when expression variables.
     * @return a hashset of every sub variables contained in the Variable.
     */
    @Override
     public Variable[] doExtractVariables() {
        return getVariables();
    }
}
