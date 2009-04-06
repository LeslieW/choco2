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

public abstract class PGlobalConstraint extends PConstraint {
	public PGlobalConstraint(String name, PVariable[] scope) {
		super(name, scope);
	}

	protected int computeObjectPositionInScope(Object object) {
		if (object instanceof PVariable)
			return getPositionInScope((PVariable) object);
		return -1;
	}

	protected int[] computeObjectPositionsInScope(Object[] objects) {
		int[] t = new int[objects.length];
		for (int i = 0; i < objects.length; i++)
			t[i] = computeObjectPositionInScope(objects[i]);
		return t;
	}

	// object is either an Integer, a PVariable or null
	protected String computeStringRepresentationOf(Object object) {
		if (object == null)
			return "nil";
		if (object instanceof PVariable)
			return ((PVariable) object).getName();
		return object.toString();
	}

}
