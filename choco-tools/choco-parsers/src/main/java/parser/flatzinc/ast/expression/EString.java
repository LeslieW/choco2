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

import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;

/*
* User : CPRUDHOM
* Mail : cprudhom(a)emn.fr
* Date : 11 janv. 2010
* Since : Choco 2.1.1
*
* Class for string expressions definition based on flatzinc-like objects.
*/
public final class EString extends Expression{

    public final String st;

    public EString(String st) {
        super(EType.STR);
        this.st = st;
    }

    @Override
    public int intValue() {
        exit();
        return 0;
    }

    @Override
    public int[] toIntArray() {
        exit();
        return null;
    }

    @Override
    public IntegerVariable intVarValue() {
        exit();
        return null;
    }

    @Override
    public IntegerVariable[] toIntVarArray() {
        exit();
        return null;
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
