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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.search.task.ordering;

import java.util.Random;

import choco.cp.solver.search.task.OrderingValSelector;
import choco.kernel.solver.constraints.global.scheduling.IPrecedence;
import choco.kernel.solver.variables.scheduling.TaskVar;

public class RandomOrdering implements OrderingValSelector {

	protected final Random randomBreakTie;
	
	public RandomOrdering(long seed) {
		super();
		randomBreakTie = new Random(seed);
	}

	protected final int nextVal() {
		return randomBreakTie.nextBoolean() ? 1 : 0;
	}
	
	protected final int getMaxVal(int vZero, int vOne) {
			if(vOne > vZero) return 1;
			else if(vOne < vZero) return 0;
			else return nextVal();
	}
	
	protected final int getMinVal( int vZero, int vOne) {
		if(vOne > vZero) return 0;
		else if(vOne < vZero) return 1;
		else return nextVal();
}
	
	@Override
	public final int getBestVal(IPrecedence p) {
		return getBestVal(p.getOrigin(), p.getDestination());
	}

	@Override
	public int getBestVal(TaskVar t1, TaskVar t2) {
		return nextVal();
	}	
	
	
}