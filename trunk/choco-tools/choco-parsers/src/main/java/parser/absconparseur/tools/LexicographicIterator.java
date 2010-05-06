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
package parser.absconparseur.tools;

/**
 * This class allows iterating all tuples in a lexicograpic order from a given set of domains.
 */
public class LexicographicIterator {
	private final int[] tuple;

	private final int[] position;

	private final int[][] domains;

	public LexicographicIterator(int[][] domains) {
		this.domains = domains;
		tuple = new int[domains.length];
		position = new int[domains.length];
	}

	public int[] getFirstTuple() {
		for (int i = 0; i < tuple.length; i++) {
			tuple[i] = domains[i][0];
			position[i] = 0;
		}
		return tuple;
	}

	public int[] getNextTupleAfter(int[] tuple) {
		for (int i = tuple.length - 1; i >= 0; i--) {
			if (position[i] < domains[i].length - 1) {
				position[i]++;
				tuple[i] = domains[i][position[i]];
				return tuple;
			}
			tuple[i] = domains[i][0];
			position[i] = 0;
		}
		return null;
	}
}
