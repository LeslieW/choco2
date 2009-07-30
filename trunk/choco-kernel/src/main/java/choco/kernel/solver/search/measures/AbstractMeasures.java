package choco.kernel.solver.search.measures;

import java.util.logging.Logger;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.search.limit.Limit;

public abstract class AbstractMeasures implements ISearchMeasures {

	public final static Logger LOGGER = ChocoLogging.getSolverLogger();
	
	@Override
	public final int getLimitCount(Limit type) {
		switch (type) {
		case TIME: return getTimeCount();
		case NODE: return getNodeCount();
		case BACKTRACK: return getBackTrackCount();
		case RESTART: return getRestartCount();
		case FAIL: return getFailCount();
		default: 
			return -1;
		}
	}

	@Override
	public String pretty() {
		StringBuilder b = new StringBuilder();
		for (Limit type : Limit.values()) {
			final int val = getLimitCount(type);
			if(val != -1) {
				b.append(val).append(' ').append(type.getUnit()).append(" ; ");
			}
		}
		return new String(b);
	}

	

}
