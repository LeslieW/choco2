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

import choco.kernel.model.variables.VariableType;

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 17 mars 2008
 * Since : Choco 2.0.0
 *
 */
public final class IntegerConstantVariable extends IntegerVariable{

	public IntegerConstantVariable(final int value) {
        super(VariableType.CONSTANT_INTEGER, new int[]{value},false, NO_CONSTRAINTS_DS);
        setName(Integer.toString(value));
        this.values = new int[]{value};
    }

    public int getValue() {
        return values[0];
    }

	@Override
	public void setLowB(final int lowB) {
		throwConstantException();
	}

	@Override
	public void setUppB(final int uppB) {
		throwConstantException();
	}


	@Override
	public boolean equals(final Object o) {
        return o instanceof IntegerConstantVariable && getValue() == ((IntegerConstantVariable) o).getValue();
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
    
    
	@Override
	public int compareTo(final Object o) {
        if(o instanceof IntegerConstantVariable){
		    final IntegerConstantVariable c = (IntegerConstantVariable)o;
            return getValue() - c.getValue();
        }else return super.compareTo(o);
	}
}
