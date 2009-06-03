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


public class PGlobalCardinality extends PGlobalConstraint {

    public Object[] table;
    private int[] tablePositionsInScope;
    public int offset; // offset of variables and values/noccurrences

    public PGlobalCardinality(String name, PVariable[] scope, Object[] table, int offset) {
		super(name, scope);
        this.table = table;
        tablePositionsInScope = computeObjectPositionsInScope(table);
        this.offset = offset;
	}

	public long computeCostOf(int[] tuple) {
        TIntIntHashMap count = new TIntIntHashMap();
        for(int v = 0;  v < offset; v++){
            Object object = table[v];
            int result = (object instanceof Integer ? (Integer) object : tuple[tablePositionsInScope[v]]);
            int nb = 1;
            if(count.containsKey(result)){
                nb += count.get(result);
            }
            count.put(result, nb);
        }

        for(int i = offset; i < table.length; i+=2){
            int value = (Integer)table[i];
            Object object = table[i+1];
            int result = (object instanceof Integer ? (Integer) object : tuple[tablePositionsInScope[i+1]]);
            if(count.get(value)!=result){
                return 1;
            }
        }
		return 0;
	}

	public String toString() {
		return super.toString() + " : globalCardinality";
	}
}