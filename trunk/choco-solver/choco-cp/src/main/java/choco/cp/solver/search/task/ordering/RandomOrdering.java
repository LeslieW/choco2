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

import choco.cp.solver.constraints.global.scheduling.precedence.ITemporalSRelation;
import choco.cp.solver.search.task.OrderingValSelector;
import choco.kernel.model.constraints.ITemporalRelation;
import choco.kernel.solver.variables.integer.IntDomainVar;
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

	protected final int getMaxVal(double vZero, double vOne) {
		if(vOne > vZero) return 1;
		else if(vOne < vZero) return 0;
		else return nextVal();
	}
	
	protected final int getMinVal( int vZero, int vOne) {
		if(vOne > vZero) return 0;
		else if(vOne < vZero) return 1;
		else return nextVal();
	}
	
	protected final int getMinVal(double vZero, double vOne) {
		if(vOne > vZero) return 0;
		else if(vOne < vZero) return 1;
		else return nextVal();
	}

	@Override
	public int getBestVal(ITemporalSRelation p) {
		return nextVal();
	}

}