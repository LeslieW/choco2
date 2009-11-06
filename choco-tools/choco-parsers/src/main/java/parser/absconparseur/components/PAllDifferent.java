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


public class PAllDifferent extends PGlobalConstraint {
	
	public PAllDifferent(String name, PVariable[] scope) {
		super(name, scope);
	}

	public long computeCostOf(int[] tuple) {
		for (int i = 0; i < tuple.length - 1; i++)
			for (int j = i + 1; j < tuple.length; j++)
				if (tuple[i] == tuple[j])
					return 1;
		return 0;
	}

	public String toString() {
		return super.toString() + " : allDifferent";
	}
}
