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

import java.util.List;

/*
* User : CPRUDHOM
* Mail : cprudhom(a)emn.fr
* Date : 7 janv. 2010
* Since : Choco 2.1.1
*
* Declaration of int list in flatzinc format, like '{2,3,5,7}' or 'var {2,4,6,8}'.
*/
public final class DManyInt extends Declaration {

    final int[] values;

    public DManyInt(Boolean isVar, List<EInt> values) {
        super(isVar, DType.INTN);
        this.values = new int[values.size()];
        for(int i = 0 ; i < values.size(); i++){
            this.values[i] = values.get(i).value;
        }
    }

    public int[] getValues() {
        return values;
    }

    @Override
    public String toString() {
        StringBuilder bf = new StringBuilder((isVar ? "var " : "") + '{');
        if(values.length>0){
            bf.append(values[0]);
            for(int i = 1; i < values.length; i++){
                bf.append(',').append(values[i]);
            }
        }
        return bf.append('}').toString();
    }
}
