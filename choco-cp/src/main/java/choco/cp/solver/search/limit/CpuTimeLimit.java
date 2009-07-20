package choco.cp.solver.search.limit;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.limit.AbstractGlobalTimeLimit;
import choco.kernel.solver.search.limit.Limit;

public class CpuTimeLimit extends AbstractGlobalTimeLimit {
	
	private final static int NANO_TO_MS = 1000000;
	
	private final ThreadMXBean thd = ManagementFactory.getThreadMXBean();
	
	public CpuTimeLimit(AbstractGlobalSearchStrategy theStrategy, int theLimit) {
		super(theStrategy, theLimit, Limit.CPU_TIME);
	}

	@Override
	public final long getTimeStamp() {
		return thd.getCurrentThreadCpuTime();
	}

	@Override
	public final void update() {
		nb = (int) ( (newh - starth) / NANO_TO_MS); 
	}
	
}



