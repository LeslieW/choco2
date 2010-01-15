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

/*
* User : CPRUDHOM
* Mail : cprudhom(a)emn.fr
* Date : 12 janv. 2010
* Since : Choco 2.1.1
*
* Class for set expressions definition based on flatzinc-like objects.
*/
public abstract class ESet extends Expression{

    protected ESet(EType typeOf) {
        super(typeOf);
    }

    public abstract int[] enumVal();

    @Override
    public final int intValue() {
        exit();
        return 0;
    }

    @Override
    public final int[] toIntArray() {
        return enumVal();
    }

    @Override
    public final IntegerVariable intVarValue() {
        exit();
        return null;
    }

    @Override
    public final IntegerVariable[] toIntVarArray() {
        exit();
        return null;
    }

    @Override
    public final SetVariable setVarValue() {
        int[] values = enumVal();
        if(values.length==0)return Choco.emptySet(); 
        return Choco.constant(enumVal());
    }

    @Override
    public final SetVariable[] toSetVarArray() {
        return new SetVariable[]{setVarValue()};
    }
}
