package choco.cp.solver.constraints.global.scheduling.cumulative;

import java.util.Arrays;

import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.TaskVar;

public class AltCumulative extends Cumulative {


	public AltCumulative(Solver solver, String name, TaskVar[] taskvars,
                         IntDomainVar[] heights, IntDomainVar[] usages,
                         IntDomainVar consumption, IntDomainVar capacity,
                         IntDomainVar uppBound) {
		super(solver, name, taskvars, usages.length, consumption, capacity, uppBound,
				ArrayUtils.append(usages, heights));
		cumulSweep = new CumulSweep(this, Arrays.asList(rtasks));
	}

	

	@Override
	public void fireTaskRemoval(IRTask rtask) {
		//Do nothing (no dynamic data-structure
	}



	@Override
	protected void checkRulesRequirement() {
		throw new UnsupportedOperationException("Alternative Task Intervals and Edge finding remain to be done");
	}
	
	

}
