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
* Date : 11 janv. 2010
* Since : Choco 2.1.1
*
* Class for int expressions definition based on flatzinc-like objects.
*/
public final class EInt extends Expression{

    public final int value;

    public EInt(String sign, String svalue) {
        super(EType.INT);
        value = Integer.parseInt(sign + svalue);
    }

    @Override
    public String toString() {
        return ""+value;
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public int[] toIntArray() {
        return new int[]{intValue()};
    }

    @Override
    public IntegerVariable intVarValue() {
        return Choco.constant(intValue());
    }

    @Override
    public IntegerVariable[] toIntVarArray() {
        return new IntegerVariable[]{intVarValue()};
    }

    @Override
    public SetVariable setVarValue() {
        exit();
        return null;
    }

    @Override
    public SetVariable[] toSetVarArray() {
        exit();
        return null;
    }
}
