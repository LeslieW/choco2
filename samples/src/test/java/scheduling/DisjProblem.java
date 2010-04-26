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
import choco.cp.solver.constraints.global.scheduling.disjunctive.Disjunctive;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 26 avr. 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
class DisjProblem extends AbstractTestProblem {

	public boolean forbidInt=false;


	public DisjProblem(IntegerVariable[] durations) {
		super(durations);
	}

	public DisjProblem(IntegerVariable[] starts, IntegerVariable[] durations) {
		super(starts, durations);
	}

	public DisjProblem() {
		super();
	}


	@Override
	protected Constraint[] generateConstraints() {
		return new Constraint[]{ Choco.disjunctive(tasks)};
	}

	@Override
	public void generateSolver() {
		generateSolver(Disjunctive.Rule.NONE);
	}

	protected void setFlags(Disjunctive.Rule rule) {
		if(rule== Disjunctive.Rule.NONE) {
			setFlags(TestDisjunctive.SETTINGS);
		}else {
			Disjunctive cstr = (Disjunctive) solver.getCstr(this.rsc);
			cstr.setSingleRule(rule);
		}
	}

	public void generateSolver(Disjunctive.Rule rule) {
		super.generateSolver();
		setFlags(rule);
	}


}
