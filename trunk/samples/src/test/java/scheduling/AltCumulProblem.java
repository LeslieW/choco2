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
class AltCumulProblem extends CumulProblem {

	protected IntegerVariable[] usages;

	protected final int type;

	public AltCumulProblem(int[] durations, int[] heights, int type) {
		super(durations, heights);
		this.type = type;
	}

	@Override
	protected Constraint[] generateConstraints() {
		return new Constraint[]{ Choco.cumulative(null, tasks, heights, usages, consumption, capacity)};
	}

	@Override
	public void initializeModel() {
		usages = choco.Choco.makeBooleanVarArray("U", durations.length);
		super.initializeModel();
		model.addConstraint(choco.Choco.eq(choco.Choco.sum(usages), tasks.length - type));

	}

}
