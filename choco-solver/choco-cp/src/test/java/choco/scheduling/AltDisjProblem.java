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
package choco.scheduling;

import static choco.Choco.*;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;

/**
 * @author Arnaud Malapert</br>
 * @since 2 mars 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
class AltDisjProblem extends DisjProblem {



	protected IntegerVariable[] usages;

	protected final int type;

	public AltDisjProblem(int[] durations, int type) {
		super(constantArray(durations));
		this.type = type;
	}

	@Override
	protected Constraint[] generateConstraints() {
		return new Constraint[]{ disjunctive(tasks, usages)};
	}

	@Override
	public void initializeModel() {
		usages = makeBooleanVarArray("U", durations.length);
		super.initializeModel();
		model.addConstraint(eq(sum(usages), tasks.length - type));

	}

}
