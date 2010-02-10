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
public class IntegerConstantVariable extends IntegerVariable implements Comparable {

	public IntegerConstantVariable(int value) {
        super(Integer.toString(value), VariableType.CONSTANT_INTEGER, value, value);
        this.values = new int[]{value};
    }

    public int getValue() {
        return values[0];
    }


	@Override
	public boolean equals(Object o) {
        if(o instanceof IntegerConstantVariable){
            return getValue() == ((IntegerConstantVariable) o).getValue();
        }else{
            return false;
        }
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
	public int compareTo(Object o) {
        if(o instanceof IntegerConstantVariable){
		    IntegerConstantVariable c = (IntegerConstantVariable)o;
            return getValue() - c.getValue();
        }else return super.compareTo(o);
	}


}
