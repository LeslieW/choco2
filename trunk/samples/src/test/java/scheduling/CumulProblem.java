/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |   (..)  |                           *
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
package scheduling;

import choco.Choco;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 26 avr. 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
class CumulProblem extends AbstractTestProblem {

	public IntegerVariable[] heights;

	public IntegerVariable capacity;

	public IntegerVariable consumption = Choco.constant(0);


	public CumulProblem(IntegerVariable[] starts, IntegerVariable[] durations, IntegerVariable[] heights) {
		super(starts, durations);
		this.heights = heights;
	}

	public CumulProblem(IntegerVariable[] durations, IntegerVariable[] heights) {
		this(null, durations, heights);
	}

	public CumulProblem(int[] durations, int[] heights) {
		this(null, choco.Choco.constantArray(durations), choco.Choco.constantArray(heights));
	}

	public final void setCapacity(int capacity) {
		this.capacity = choco.Choco.constant(capacity);
	}


	@Override
	protected Constraint[] generateConstraints() {
		return new Constraint[]{Choco.cumulative(null, tasks, heights, consumption, capacity)};
	}

	@Override
	public void generateSolver() {
		super.generateSolver();
		this.setFlags(TestCumulative.SETTINGS);
	}

}
