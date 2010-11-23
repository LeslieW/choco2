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

import choco.Choco;
import choco.cp.solver.constraints.global.scheduling.disjunctive.Disjunctive;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Configuration;

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

	public final void generateSolver() {
		generateSolver(Disjunctive.Policy.DEFAULT);
	}
	
	public void generateSolver(Disjunctive.Policy rule) {
		this.generateSolver(rule, null);
	}
	
	public void generateSolver(Disjunctive.Policy rule, Configuration conf) {
		super.generateSolver(conf);
		Disjunctive cstr = (Disjunctive) solver.getCstr(this.rsc);
		setFlags(TestDisjunctive.SETTINGS);
		cstr.setFilteringPolicy(rule);
	}


}
