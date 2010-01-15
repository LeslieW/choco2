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
package parser.flatzinc.ast.declaration;

import parser.flatzinc.ast.expression.EInt;

/*
* User : CPRUDHOM
* Mail : cprudhom(a)emn.fr
* Date : 7 janv. 2010
* Since : Choco 2.1.1
*
* Declaration of int in flatzinc format, like '1..3' or 'var 2..6'.
*
*/
public final class DInt2 extends Declaration {

    final int low,upp;

    public DInt2(Boolean isVar, EInt v1, EInt v2){
        super(isVar, DType.INT2);
        low = v1.value;
        upp = v2.value;
    }

    public int getLow() {
        return low;
    }

    public int getUpp() {
        return upp;
    }

    @Override
    public String toString() {
        return (isVar?"var ":"") +low+".."+upp;
    }
}
