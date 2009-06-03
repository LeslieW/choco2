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
package parser.absconparseur.components;

import gnu.trove.TIntIntHashMap;

import java.util.HashMap;


public class PLexLess extends PGlobalConstraint {

    public Object[] table;
    private int[] tablePositionsInScope;
    public int offset; // offset of variables and values/noccurrences

    public PLexLess(String name, PVariable[] scope, Object[] table, int offset) {
		super(name, scope);
        this.table = table;
        tablePositionsInScope = computeObjectPositionsInScope(table);
        this.offset = offset;
	}

	public long computeCostOf(int[] tuple) {
        for(int v = 0;  v < offset; v++){
            Object o1 = table[v];
            int r1 = (o1 instanceof Integer ? (Integer) o1 : tuple[tablePositionsInScope[v]]);
            Object o2 = table[v+offset];
            int r2 = (o2 instanceof Integer ? (Integer) o2 : tuple[tablePositionsInScope[v+offset]]);
            if(r1<r2){
                return 0;
            }else if(r1>r2){
                return 1;
            }
        }
		return 1;
	}

	public String toString() {
		return super.toString() + " : lexLess";
	}
}