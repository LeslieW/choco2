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
package choco.cp.solver.search.integer.varselector;

import choco.kernel.solver.Solver;
import choco.kernel.solver.search.integer.IntHeuristicIntVarSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.ArrayList;
import java.util.Random;

public class RandomIntVarSelector extends IntHeuristicIntVarSelector {

	private ArrayList<IntDomainVar> reuseList = new ArrayList<IntDomainVar>();
	protected final Random random;

	/**
	 * Creates a new random-based integer domain variable selector
	 */
	public RandomIntVarSelector(Solver solver) {
		super(solver);
		this.random = new Random();
	}

    /**
	 * Creates a new random-based integer domain variable selector with the specified seed
	 * (to make the experiment determinist)
	 */
	public RandomIntVarSelector(Solver solver, long seed) {
		super(solver);
		this.random = new Random(seed);
	}

	public RandomIntVarSelector(Solver solver, IntDomainVar[] vs, long seed) {
		super(solver, vs);
		this.random = new Random(seed);
	}

    @Override
    public int getHeuristic(IntDomainVar v) {
        return random.nextInt();
    }

}
