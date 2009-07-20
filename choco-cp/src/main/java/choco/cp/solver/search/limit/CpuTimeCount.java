package choco.cp.solver.search.limit;

import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.limit.Limit;

public final class CpuTimeCount extends CpuTimeLimit {


	public CpuTimeCount(AbstractGlobalSearchStrategy theStrategy) {
		super(theStrategy, Integer.MAX_VALUE);
		limitMask = 0;
	}


	/**
	 * should update the value before recording solution as the event are not handled.
	 */
	@Override
	public int getUpdatedNb() {
		newh = getTimeStamp();
		update();
		return nb;
	}
}
