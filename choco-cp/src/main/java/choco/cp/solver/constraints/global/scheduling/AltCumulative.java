package choco.cp.solver.constraints.global.scheduling;

import java.util.Arrays;

import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;

public class AltCumulative extends Cumulative {

	private final int usageOffset;
	
	public final int nbRequired;
	
	public AltCumulative(String name, TaskVar[] taskvars,
			IntDomainVar[] heights, IntDomainVar[] usages,
			IntDomainVar consumption, IntDomainVar capacity,
			IntDomainVar uppBound) {
		super(name, taskvars, heights, consumption, capacity, uppBound,
				usages);
		nbRequired = getNbTasks() - usages.length;
		usageOffset = getTaskIntVarOffset() + getNbTasks() + 2;
		cumulSweep = new CumulSweep(this, Arrays.asList(rtasks));
	}
	
	
	
	@Override
	protected void checkRulesRequirement() {
		throw new UnsupportedOperationException("Alternative Task Intervals and Edge finding remain to be done");
	}



	@Override
	protected final int getUsageIndex(int taskIdx) {
		return taskIdx < nbRequired ? indexUnit : usageOffset + taskIdx - nbRequired;
	}

	

}
