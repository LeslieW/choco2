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
package parser.flatzinc.ast.expression;

import choco.Choco;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;

import java.util.HashMap;

/*
* User : CPRUDHOM
* Mail : cprudhom(a)emn.fr
* Date : 11 janv. 2010
* Since : Choco 2.1.1
*
* Class for identifier expressions definition based on flatzinc-like objects.
*/
public final class EIdentifier extends Expression{

    public final String value;

    public final Object object;

    public EIdentifier(HashMap<String, Object> map, String s) {
        super(EType.IDE);
        this.value = s;
        object = map.get(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int intValue() {
        return ((Integer)object).intValue();
    }

    @Override
    public int[] toIntArray() {
        if(bool_arr.isInstance(object)){
            return bools_to_ints((boolean[])object);
        }
        return (int[])object;
    }

    @Override
    public IntegerVariable intVarValue() {
        return (IntegerVariable)object;
    }

    @Override
    public IntegerVariable[] toIntVarArray() {
        if(object.getClass().isArray()){
            //Can be array of int => array of IntegerConstantVariable
            if(int_arr.isInstance(object)){
                return Choco.constantArray((int[])object);
            }else if(bool_arr.isInstance(object)){
                return Choco.constantArray(bools_to_ints((boolean[])object));
            }
            return (IntegerVariable[])object;
        }
        exit();
        return null;
    }

    @Override
    public SetVariable setVarValue() {
        return (SetVariable)object;
    }

    @Override
    public SetVariable[] toSetVarArray() {
        return (SetVariable[])object;
    }

    private static int[] bools_to_ints(boolean[] bar) {
        final int[] values = new int[bar.length];
        for (int i = 0; i < bar.length; i++) {
            values[i] = bar[i] ? 1 : 0;
        }
        return values;
    }
}
