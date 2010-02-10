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
import gnu.trove.TIntArrayList;
import gnu.trove.TIntProcedure;

import java.util.ArrayList;
import java.util.Set;
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

	private TIntArrayList dclique = new TIntArrayList();

	/** number of required task in the disj. clique*/
	private int dcnr;

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




	protected final void initializeDecomposition(RscData rdata) {
		dclique.reset();
		//if the capacity is even, then we can add at most one task t1 such that h1 == limit.
		//Indeed, for all other task t2, h1 + h2 >= limit + (limit+1) > 2*limit = capa. 
		boolean addExtraTask = capacity.getSup() % 2 == 0;
		final int limit = capacity.getSup()/2;
		if(limit > 0) {
			final int n  = rdata.getNbTasks();
			//required tasks			
			for (int i = 0; i < n; i++) {
				final int h = heights[i].getInf();
				if( h > limit) {
					dclique.add(i);
				} else if ( addExtraTask && h == limit) {
					dclique.add(i);
					addExtraTask = false;
				}else if( h < 0) {
					//the height is negative (producer tasks): cancel disjunction.					
					dclique.reset();
					return;
				}
			}
			dcnr = dclique.size();
			final int nreq = rdata.getNbRequiredTasks();
			dclique.forEachDescending( new TIntProcedure() {

				@Override
				public boolean execute(int arg0) {
					if( arg0 > nreq) {
						dcnr--;
						return true;
					}
					return false;
				}

			});
		}
	}


	
	protected final void makeDisjunctions(CPSolver solver) {
		for (int i = 0; i < dcnr; i++) {
			for (int j = i+1; j < dcnr; j++) {
				constraints.add( solver.preceding(null, tasks[dclique.get(i)], tasks[dclique.get(j)]));
			}
		}
	}


	protected final void makeDisjunctive(CPSolver solver, RscData rdata, Set<String> options) {
		final int n = dclique.size();
		if(n > 3) {
			//make tasks array
			final TaskVar[] tv = new TaskVar[n];
			for (int i = 0; i < n; i++) {
				tv[i] = tasks[dclique.get(i)];
			}
			Disjunctive cstr;
			if( dcnr < n ) {
				//make usages array
				final IntDomainVar[] uv = new IntDomainVar[n - dcnr];
				for (int i = dcnr; i < n; i++) {
					uv[i - dcnr] = usages[ dclique.get(i)];
				}
				//alternative disj.
				cstr = new AltDisjunctive("disj-"+rdata.getRscName(), tv, uv, uppBound);
			}else {
				//disj.
				cstr = new Disjunctive("disj-"+rdata.getRscName(), tv, uppBound);
			}
			cstr.getFlags().readDisjunctiveOptions(options);
			constraints.add(cstr);
		}else if( n > 1) {
			//only post binary disjunction
			makeDisjunctions(solver);
		}
	}

	protected final void makeCumulative(RscData rdata, Set<String> options) {
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
			Variable[] variables, RscData rdata, Set<String> options) {
		initializeDecomposition(rdata);
		makeDisjunctions(solver);
		makeGlobalConstraint(solver, variables, rdata, options);
	}

	@Override
	protected void makeGlobalConstraint(CPSolver solver,
			Variable[] variables, RscData rdata, Set<String> options) {
		makeConsCapaConstraint(solver);
		makeCumulative(rdata, options);
	}

	@Override
	protected void makeMixedConstraint(CPSolver solver,
			Variable[] variables, RscData rdata, Set<String> options) {
		initializeDecomposition(rdata);
		makeDisjunctive(solver, rdata, options);
		makeGlobalConstraint(solver, variables, rdata, options);
	}



}
