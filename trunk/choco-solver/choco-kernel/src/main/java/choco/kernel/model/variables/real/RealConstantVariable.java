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

    private static String toString(double v1, double v2){
        StringBuffer st = new StringBuffer("[");
        st.append(Double.toString(v1)).append(",").append(Double.toString(v2)).append("]");
        return st.toString();
    }

	public RealConstantVariable(double value) {
		super(toString(value, value), VariableType.CONSTANT_DOUBLE, value, value);
		this.setValue(value);
	}


	public RealConstantVariable(double value1, double value2) {
		super(toString(value1, value2),
                VariableType.CONSTANT_DOUBLE, value1, value2);
		this.setValue(value1, value2);
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

	private void setValue(double value) {
		this.setLowB(value);
		this.setUppB(value);
	}

    private void setValue(double value1, double value2) {
		this.setLowB(value1);
		this.setUppB(value2);
	}


}