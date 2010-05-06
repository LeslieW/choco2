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
* Class for array index expressions definition based on flatzinc-like objects.
*/
public final class EIdArray extends Expression{

    final String name;
    final EInt index;
    final Object object;

    public EIdArray(HashMap<String, Object> map, String id, EInt i) {
        super(EType.IDA);
        this.name = id;
        this.index = i;

        Object array = map.get(name);
        if(int_arr.isInstance(array)){
            object = ((int[])array)[index.value-1];
        }else if(bool_arr.isInstance(array)){
            object = ((int[])array)[index.value-1];
        }else{
            object = ((Object[])array)[index.value-1];    
        }
    }

    @Override
    public String toString() {
        return name+ '[' +index.toString()+ ']';
    }

    @Override
    public int intValue() {
        return (Integer) object;
    }

    @Override
    public int[] toIntArray() {
        return (int[])object;
    }

    @Override
    public IntegerVariable intVarValue() {
        if(Integer.class.isInstance(object)){
            return Choco.constant((Integer)object);
        }else if(Boolean.class.isInstance(object)){
            return Choco.constant(((Boolean)object)?1:0);
        }
        return (IntegerVariable)object;
    }

    @Override
    public IntegerVariable[] toIntVarArray() {
        return (IntegerVariable[])object;
    }

    @Override
    public SetVariable setVarValue() {
        return (SetVariable)object;
    }

    @Override
    public SetVariable[] toSetVarArray() {
        return (SetVariable[])object;
    }

}
