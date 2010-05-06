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

import parser.absconparseur.Toolkit;

public class PElement extends PGlobalConstraint {
	private final PVariable index;

	private final Object[] table;

	private final Object value;

	private static final int offset=1; // by default, indexing starts from 1

	private final int indexPositionInScope;

	private final int[] tablePositionsInScope;
	
	private final int valuePositionInScope;

	public PElement(String name, PVariable[] scope, PVariable indexVariable, Object[] table, Object value) {
		super(name, scope);
		this.index = indexVariable;
		this.table = table;
		this.value = value;
		indexPositionInScope = Toolkit.searchFirstObjectOccurrenceIn(indexVariable, scope);
		tablePositionsInScope = computeObjectPositionsInScope(table);
		valuePositionInScope = Toolkit.searchFirstObjectOccurrenceIn(value, scope);
	}

	public long computeCostOf(int[] tuple) {
		int indexInTable = tuple[indexPositionInScope]-offset;
		Object object = table[indexInTable];
		int result = (object instanceof Integer ? (Integer) object : tuple[tablePositionsInScope[indexInTable]]);
		boolean satisfied = result == (value instanceof Integer ? (Integer) value : tuple[valuePositionInScope]);
		return satisfied ? 0 : 1;
	}

	public String toString() {
		StringBuilder s = new StringBuilder(128);
        s.append(super.toString()).append(" : element\n\t");
        s.append("index=").append(index.getName()).append("  table=");
		for (int i = 0; i < table.length; i++)
            s.append(computeStringRepresentationOf(table[i])).append(' ');
        s.append("  value=").append(computeStringRepresentationOf(value));
		return s.toString();
	}

    public PVariable getIndex() {
        return index;
    }

    public Object[] getTable() {
        return table;
    }

    public Object getValue() {
        return value;
    }
}
