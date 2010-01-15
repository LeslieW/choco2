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

/*
* User : CPRUDHOM
* Mail : cprudhom(a)emn.fr
* Date : 8 janv. 2010
* Since : Choco 2.1.1
*
* Class for set expressions definition based on flatzinc-like objects,
* defined with two EInt.
*/
public final class ESetBounds extends ESet{
    
    final int low;
    final int upp;

    public ESetBounds(EInt sl, EInt su) {
        super(EType.SET_B);
        low = sl.value;
        upp = su.value;
    }

    public int[] enumVal(){
        int[] values = new int[upp - low +1];
        for(int i = low; i <= upp; i++){
            values[i-low] = i;
        }
        return values;
    }

    @Override
    public String toString() {
        return low+".."+upp;
    }
}
