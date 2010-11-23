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
import static choco.kernel.common.util.tools.VariableUtils.getTaskVar;

import java.util.List;

import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.scheduling.cumulative.AltCumulative;
import choco.cp.solver.constraints.global.scheduling.cumulative.Cumulative;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.global.scheduling.ResourceParameters;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;


/**
 * @author Arnaud Malapert</br> 
 * @since 23 janv. 2009 version 2.0.3</br>
 * @version 2.0.1</br>
 */
public final class CumulativeManager extends AbstractResourceManager {

	@Override
	protected SConstraint makeConstraint(CPSolver s,
			Variable[] variables, ResourceParameters rdata, List<String> options) {
		final int n = rdata.getUsagesOffset();
		final TaskVar[] tasks = getTaskVar(s, variables, 0, n);
		final IntDomainVar[] usages = getIntVar(s, variables, n, rdata.getHeightsOffset());
		final IntDomainVar[] heights = getIntVar(s, variables, rdata.getHeightsOffset() ,rdata.getConsOffset());
		final IntDomainVar consumption = s.getVar( (IntegerVariable) variables[rdata.getConsOffset()]);
		final IntDomainVar capacity = s.getVar( (IntegerVariable) variables[rdata.getCapaOffset()]);
		final IntDomainVar horizon = getHorizon(s, variables, rdata);
		
		if(consumption.getSup() > capacity.getInf()) {
			s.post(s.leq(consumption, capacity));
		}

		final Cumulative cstr = (
				rdata.getNbOptionalTasks() > 0 ? 
						new AltCumulative(s, rdata.getRscName(), tasks, heights, usages, consumption, capacity, horizon) :
							new Cumulative(s, rdata.getRscName(), tasks, heights , consumption, capacity, horizon)
		);
		cstr.readOptions(options);
		return cstr;
	}

}
