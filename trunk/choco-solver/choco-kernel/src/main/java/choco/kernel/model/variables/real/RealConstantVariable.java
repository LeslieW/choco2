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

import choco.kernel.model.variables.VariableType;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 17 mars 2008
 * Time: 18:36:03
 * MOdel object for real constant
 */
public class RealConstantVariable extends RealVariable {


	public RealConstantVariable(double value) {
		this(value,value);		
	}


	public RealConstantVariable(double value1, double value2) {
		super(VariableType.CONSTANT_DOUBLE, false, NO_CONSTRAINTS_DS, value1, value2);
		setName(String.valueOf(getValue()));
	}

    /**
     * pretty printing of the object. This String is not constant and may depend on the context.
     *
     * @return a readable string representation of the object
     */
    @Override
    public String pretty() {
        return name;
    }

    public double getValue() {
		return lowB;
	}
    
	@Override
	public void setLowB(double lowB) {
		throwConstantException();
	}

	@Override
	public void setUppB(double uppB) {
		throwConstantException();
	}

}