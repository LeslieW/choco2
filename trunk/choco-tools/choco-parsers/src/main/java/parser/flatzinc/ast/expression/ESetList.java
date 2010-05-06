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

import java.util.List;

/*
* User : CPRUDHOM
* Mail : cprudhom(a)emn.fr
* Date : 8 janv. 2010
* Since : Choco 2.1.1
*
* Class for identifier expressions definition based on flatzinc-like objects,
* defined with a list of EInt.
*/
public final class ESetList extends ESet{

    final int[] values;

    public ESetList(List<EInt> sl) {
        super(EType.SET_L);
        values = new int[sl.size()];
        for(int i = 0; i < sl.size(); i++){
            values[i] = sl.get(i).value;
        }
    }

    @Override
    public int[] enumVal() {
        return values;
    }

    @Override
    public String toString() {
        StringBuilder bf = new StringBuilder("{");
        if(values.length>0){
            bf.append(values[0]);
            for(int i = 1; i < values.length; i++){
                bf.append(',').append(values[i]);
            }
        }
        return bf.append('}').toString();
    }
}