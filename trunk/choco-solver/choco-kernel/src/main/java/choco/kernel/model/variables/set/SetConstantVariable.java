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

import choco.kernel.model.ModelException;
import choco.kernel.model.variables.VariableType;
import choco.kernel.model.variables.integer.IntegerConstantVariable;

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 17 mars 2008
 * Since : Choco 2.0.0
 *
 */
public class SetConstantVariable extends SetVariable {

    private static String toString(int[] values){
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        for(int i = 0; i < values.length; i++){
            sb.append(i);
            if(i<values.length-1)sb.append(",");
        }
        sb.append("}");
        return sb.toString();
    }

	public SetConstantVariable(IntegerConstantVariable card, int... value) {
        super(toString(value), VariableType.CONSTANT_SET, value, card);
    }

    public int[] getValues() {
        return values;
    }

    public int getLowB() {
        if(values.length>0)return values[0];
        throw new ModelException("Cannot access lower bound of an empty set");
    }

    public int getUppB() {
        if(values.length>0)return values[values.length-1];
        throw new ModelException("Cannot access lower bound of an empty set");
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
	public boolean equals(Object o) {
        if(o instanceof SetConstantVariable){
            return values == ((SetConstantVariable) o).getValues();
        }else{
            return false;
        }
    }
}
