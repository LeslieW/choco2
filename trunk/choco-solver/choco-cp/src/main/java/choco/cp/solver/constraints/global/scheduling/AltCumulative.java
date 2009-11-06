package choco.cp.solver.constraints.global.scheduling;

import java.util.Arrays;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;

public class AltCumulative extends Cumulative {


	public AltCumulative(String name, TaskVar[] taskvars,
			IntDomainVar[] heights, IntDomainVar[] usages,
			IntDomainVar consumption, IntDomainVar capacity,
			IntDomainVar uppBound) {
		super(name, taskvars, heights, consumption, capacity, uppBound,
				usages);
		cumulSweep = new CumulSweep(this, Arrays.asList(rtasks));
	}
	
	
	
	
	@Override
	public int getFilteredEventMask(int idx) {
		return idx < 4* taskvars.length ? EVENT_MASK : IntVarEvent.INSTINTbitvector;
	}




	@Override
	protected void checkRulesRequirement() {
		throw new UnsupportedOperationException("Alternative Task Intervals and Edge finding remain to be done");
	}


	@Override
	protected final int getUsageIndex(int tidx) {
		final int nbRequired = computeNbRequired();
		return  tidx < nbRequired ? indexUnit : 4 * startOffset+ 2 +  tidx - nbRequired;
	}

	private final int computeNbRequired() {
		return 5 * taskvars.length + 4 - vars.length;
	}
	
	@Override
	protected boolean isRegular(int[] tuple, int tidx) {
		return tuple[ getUsageIndex(tidx)] == 1;
	}
	
	

}
