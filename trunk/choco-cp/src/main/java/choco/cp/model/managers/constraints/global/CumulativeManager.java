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

import choco.cp.solver.CPSolver;
import choco.cp.solver.SettingType;
import choco.cp.solver.constraints.global.scheduling.AltCumulative;
import choco.cp.solver.constraints.global.scheduling.AltDisjunctive;
import choco.cp.solver.constraints.global.scheduling.Cumulative;
import choco.cp.solver.constraints.global.scheduling.Disjunctive;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.global.scheduling.RscData;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;



/**
 * @author Arnaud Malapert</br> 
 * @since 23 janv. 2009 version 2.0.3</br>
 * @version 2.0.1</br>
 */
public class CumulativeManager extends AbstractResourceManager {

	@Override
	public SConstraint makeConstraint(Solver solver, Variable[] variables,
			Object parameters, HashSet<String> options) {
		if (solver instanceof CPSolver) {
			final CPSolver s = (CPSolver) solver;
			final RscData param =  (RscData) parameters;
			final int n = param.getNbTasks();
			final int no = param.getNbOptionalTasks();
			final TaskVar[] tasks = readTaskVar(s, variables, 0, n);
			final IntDomainVar[] heights = readIntVar(s, variables, n, n);
			final IntDomainVar[]  usages = readIntVar(s, variables, 2*n, no);
			final IntDomainVar uppBound =  getUppBound(s, param);
			final IntDomainVar consumption = s.getVar( (IntegerVariable) variables[2*n + no]);
			final IntDomainVar capacity =  s.getVar( (IntegerVariable) variables[2*n + no + 1]);
			
			//redundant cons <= capa constraint
			if(consumption.getSup()> capacity.getInf()) {
				solver.post(s.leq(consumption, capacity));
			}
			//build cumulative
			final Cumulative cstr = usages.length == 0 ? new Cumulative(param.getRscName(), tasks, heights, consumption, capacity, uppBound) :
				new AltCumulative(param.getRscName(), tasks, heights, usages, consumption, capacity, uppBound);
			//read settings
			readCumulativeSettings(options, cstr.getFlags());
			if(cstr.getFlags().isEmpty() && usages.length == 0 && cstr.hasOnlyPosisiveHeights()) {
				//default settings when not a producer/consumer or alternative resource
				cstr.getFlags().set(SettingType.TASK_INTERVAL);
			}
			//extract implied disjunctive if the resource is not a producer/consumer resource.
			//otherwise producers break the disjunctive condition
			if(options.contains(SettingType.EXTRACT_DISJ.getOptionName())
			   && cstr.hasOnlyPosisiveHeights()	) {
				List<TaskVar> vars = new LinkedList<TaskVar>();
				List<IntDomainVar> usg = new LinkedList<IntDomainVar>();
				final int limit = capacity.getSup()/2;
				for (int i = 0; i < heights.length; i++) {
					if(heights[i].getInf() > limit) { 
						vars.add(tasks[i]);
						if(i>= param.getNbRequiredTasks()) {usg.add(usages[i]);}
					}
				}
				if(vars.size()>2) {
					TaskVar[] dtasks = vars.toArray(new TaskVar[vars.size()]);	
					Disjunctive disj = usg.isEmpty() ? new Disjunctive("exDisj-"+param.getRscName(), dtasks, uppBound) :
						new AltDisjunctive("exDisj-"+param.getRscName(), dtasks, usg.toArray(new IntDomainVar[usg.size()]), uppBound);
					 readDisjunctiveSettings(options, disj.getFlags());
					 s.post(disj);
				}
			}
			return cstr;
		}
		return null;
	}
	
	

}
