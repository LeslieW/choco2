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
package choco.cp.solver.search.task;

import java.util.Random;

import choco.cp.solver.search.integer.branching.IRandomBreakTies;

public class Centroid implements PrecValSelector, IRandomBreakTies {

	private Random randomBreakTies;
	
	public Centroid() {
		super();
		randomBreakTies = new Random();
	}

	public Centroid(long seed) {
		super();
		setRandomBreakTies(seed);
	}

	@Override
	public void cancelRandomBreakTies() {
		randomBreakTies = null;
	}

	@Override
	public void setRandomBreakTies(long seed) {
		randomBreakTies = new Random(seed);
		
	}

	@Override
	public int getBestVal(StoredPrecedence precedence) {
		final double c1=precedence.t1.getCentroid();
		final double c2=precedence.t2.getCentroid();
		if(c1<c2) {return 1;}
		else if(c1>c2) {return 0;}
		else if(randomBreakTies == null) {return 0;}
		else {return randomBreakTies.nextInt(2);}
	}

}
