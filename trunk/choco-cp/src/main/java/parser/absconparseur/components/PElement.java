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

public class PElement extends PGlobalConstraint {
	private PVariable index;

	private Object[] table;

	private Object value;

	private int indexPositionInScope;

	private int[] tablePositionsInScope;
	
	private int valuePositionInScope;

	
	public PElement(String name, PVariable[] scope, PVariable indexVariable, Object[] table, Object value) {
		super(name, scope);
		this.index = indexVariable;
		this.table = table;
		this.value = value;
		indexPositionInScope = getPositionInScope(indexVariable);
		tablePositionsInScope = computeObjectPositionsInScope(table);
		valuePositionInScope = computeObjectPositionInScope(value);
	}


	public int computeCostOf(int[] tuple) {
		int indexInTable = tuple[indexPositionInScope];
		Object object = table[indexInTable];
		int result = (object instanceof Integer ? (Integer) object : tuple[tablePositionsInScope[indexInTable]]);
		boolean satisfied = result == (value instanceof Integer ? (Integer) value : tuple[valuePositionInScope]);
		return satisfied ? 0 : 1;
	}

	public String toString() {
		String s = super.toString() + " : element\n\t";
		s += "index=" + index.getName() + "  table=";
		for (int i = 0; i < table.length; i++)
			s += computeStringRepresentationOf(table[i]) + " ";
		s += "  value=" + computeStringRepresentationOf(value);
		return s;
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
