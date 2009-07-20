package choco.cp.solver.search.limit;

import java.util.logging.Level;

import choco.kernel.solver.search.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.limit.AbstractLimitManager;
import choco.kernel.solver.search.limit.Limit;


public class LimitManager extends AbstractLimitManager {

	
	private final static int SEC_TO_MS = 1000;
	/**
	 * Initialize a default limit manager monitoring:
	 * <ul>
	 * <li>time,</li>
	 * <li>node</li>
	 * </ul>
	 */
	public LimitManager() {
		super();
		monitorLimit(Limit.TIME, true);
		monitorLimit(Limit.NODE, true);
	}

	
	@Override
	public AbstractGlobalSearchLimit makeLimit(AbstractGlobalSearchStrategy strategy, Limit type, int limit) {
		switch (type) {
		case NODE:
			return limit == Integer.MAX_VALUE ? new NodeCount(strategy) : new NodeLimit(strategy, limit);
		case BACKTRACK:
			return limit == Integer.MAX_VALUE ? new BackTrackCount(strategy) : new BackTrackLimit(strategy, limit);
		case FAIL:
			return new FailLimit(strategy, limit);
		case TIME:
			return (limit == Integer.MAX_VALUE ? new TimeCount(strategy) : new TimeLimit(strategy, limit));
		case CPU_TIME:
			return (limit == Integer.MAX_VALUE ? new CpuTimeCount(strategy) : new CpuTimeLimit(strategy, limit));
		default:
			LOGGER.log(Level.WARNING, "cant create limit {0}", type);
		return null;
		}
	}
}
