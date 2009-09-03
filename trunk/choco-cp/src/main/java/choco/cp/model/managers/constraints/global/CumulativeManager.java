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
package choco.cp.model.managers.constraints.global;

import static choco.kernel.common.util.tools.VariableUtils.getIntVar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.scheduling.AltCumulative;
import choco.cp.solver.constraints.global.scheduling.AltDisjunctive;
import choco.cp.solver.constraints.global.scheduling.Cumulative;
import choco.cp.solver.constraints.global.scheduling.Disjunctive;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.constraints.global.scheduling.RscData;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;



/**
 * @author Arnaud Malapert</br> 
 * @since 23 janv. 2009 version 2.0.3</br>
 * @version 2.0.1</br>
 */
public class CumulativeManager extends AbstractResourceManager {

	protected IntDomainVar capacity, consumption;

	protected IntDomainVar[] heights;

	protected final List<TaskVar> dtasks = new ArrayList<TaskVar>();

	protected final List<IntDomainVar> dusages = new ArrayList<IntDomainVar>();

	@Override
	protected void initialize(CPSolver s,
			Variable[] variables, RscData rdata) {
		super.initialize(s, variables, rdata);
		final int b = rdata.getNbTasks() + rdata.getNbOptionalTasks();
		final int e = b + rdata.getNbTasks();
		heights = getIntVar(s, variables, b ,e);
		consumption = s.getVar( (IntegerVariable) variables[e]);
		capacity =  s.getVar( (IntegerVariable) variables[e + 1]);
	}



	private final void clearClique() {
		dtasks.clear();
		dusages.clear();

	}
	protected final void initializeDecomposition(RscData rdata) {
		clearClique();
		final int limit = capacity.getSup()/2;
		if(limit > 0) {
			final int n  = rdata.getNbTasks();
			final int nreq = rdata.getNbRequiredTasks();
			//required tasks			
			for (int i = 0; i < nreq; i++) {
				final int h = heights[i].getInf();
				if( h > limit) {
					dtasks.add(tasks[i]);
				}else if( h < 0) {
					//the height is negative (producer tasks): cancel disjunction.					
					clearClique();
					return;
				}
			}
			//optional tasks
			for (int i = nreq; i < n; i++) {
				final int h = heights[i].getInf();
				if( h > limit) {
					dtasks.add(tasks[i]);
					dusages.add(usages[ i - nreq]);
				}else if( h < 0) {
					//the height is negative (producer tasks): cancel disjunction.					
					clearClique();
					return;
				}
			}
		}
	}

	protected final void makeDisjunctions(CPSolver solver) {
		final int n = dtasks.size() - dusages.size();
		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				constraints.add( solver.preceding(null, dtasks.get(i), 0, dtasks.get(j), 0));
			}
		}
	}

	protected final void makeDisjunctive(CPSolver solver, RscData rdata, HashSet<String> options) {
		final int n = dtasks.size();
		final int no = dusages.size();
		if(n > 2) {
			final TaskVar[] tv = dtasks.toArray(new TaskVar[n]);
			Disjunctive cstr;
			if( no > 0) {
				final IntDomainVar[] uv = dusages.toArray(new IntDomainVar[no]);
				cstr = new AltDisjunctive("disj-"+rdata.getRscName(), tv, uv, uppBound);
			}else {
				cstr = new Disjunctive("disj-"+rdata.getRscName(), tv, uppBound);
			}
			cstr.getFlags().readDisjunctiveOptions(options);
			constraints.add(cstr);
		}else if( n == 2 && no == 0) {
			constraints.add( solver.preceding(null, dtasks.get(0), 0, dtasks.get(1), 0));
		}
	}

	protected final void makeCumulative(RscData rdata, HashSet<String> options) {
		final Cumulative cstr = (
				rdata.getNbOptionalTasks() > 0 ? 
						new AltCumulative(rdata.getRscName(), tasks, heights, usages, consumption, capacity, uppBound) : 
							new Cumulative(rdata.getRscName(), tasks, heights ,  consumption, capacity, uppBound)
		);
		cstr.getFlags().readCumulativeOptions(options);
		constraints.addFirst(cstr);
	}

	protected final void makeConsCapaConstraint(CPSolver s) {
		if(consumption.getSup()> capacity.getInf()) {
			constraints.add(s.leq(consumption, capacity));
		}
	}

	@Override
	protected void makeDecompositionConstraint(CPSolver solver,
			Variable[] variables, RscData rdata, HashSet<String> options) {
		initializeDecomposition(rdata);
		makeDisjunctions(solver);
		makeGlobalConstraint(solver, variables, rdata, options);
	}

	@Override
	protected void makeGlobalConstraint(CPSolver solver,
			Variable[] variables, RscData rdata, HashSet<String> options) {
		makeConsCapaConstraint(solver);
		makeCumulative(rdata, options);
	}

	@Override
	protected void makeMixedConstraint(CPSolver solver,
			Variable[] variables, RscData rdata, HashSet<String> options) {
		initializeDecomposition(rdata);
		makeDisjunctive(solver, rdata, options);
		makeGlobalConstraint(solver, variables, rdata, options);
	}



}
