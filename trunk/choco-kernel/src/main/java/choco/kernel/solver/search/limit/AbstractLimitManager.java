package choco.kernel.solver.search.limit;

import static choco.kernel.solver.search.limit.AbstractGlobalSearchLimit.END_NODE;
import static choco.kernel.solver.search.limit.AbstractGlobalSearchLimit.NEW_NODE;
import gnu.trove.TIntObjectHashMap;
import gnu.trove.TObjectIntHashMap;
import gnu.trove.TObjectIntProcedure;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solution;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.GlobalSearchLimit;
import choco.kernel.solver.search.measures.AbstractMeasures;
import choco.kernel.solver.search.restart.UniversalRestartStrategy;


public abstract class AbstractLimitManager extends AbstractMeasures implements GlobalSearchLimit {

	public final static Logger LOGGER = ChocoLogging.getSearchLogger();

	protected AbstractGlobalSearchStrategy searchStrategy;

	protected final TObjectIntHashMap<Limit>  solverSettings = new TObjectIntHashMap<Limit>(10);

	protected UniversalRestartStrategy restartStrategy;

	protected Limit restartLimit;

	protected final List<AbstractGlobalSearchLimit> limits = new LinkedList<AbstractGlobalSearchLimit>();

	protected final List<AbstractGlobalSearchLimit> newNodeLimits = new LinkedList<AbstractGlobalSearchLimit>();

	protected final List<AbstractGlobalSearchLimit> endNodeLimits = new LinkedList<AbstractGlobalSearchLimit>();

	public AbstractLimitManager() {
		super();
	}

	@Override
	public final int getLimitValue(Limit type) {
		return AbstractGlobalSearchLimit.getLimitValue(limits, type);
	}

	public final int getLimitIndex(Limit type) {
		return AbstractGlobalSearchLimit.getLimitIndex(limits, type);
	}


	@Override
	public final int getIterationCount() {
		LOGGER.warning("not yet implemented");
		return -1;
	}



	public final int getNbLimits() {
		return limits.size();
	}

	public final List<AbstractGlobalSearchLimit> getLimitsView() {
		return Collections.unmodifiableList(limits);
	}

	public final AbstractGlobalSearchLimit getLimit(Limit limit) {
		return AbstractGlobalSearchLimit.getLimit(limits, limit);
	}

	public final void MonitorAllLimits() {
		for (Limit type : Limit.values()) {
			monitorLimit(type, true);
		}
	}

	public final void monitorLimit(Limit type, boolean b) {
		if(b) {solverSettings.putIfAbsent(type, Integer.MAX_VALUE);}
		else {solverSettings.remove(type);}
	}

	public final void setLimit(Limit type, int limit) {
		solverSettings.put(type, limit);
	}

	public final void setRestartStrategy(UniversalRestartStrategy restartStrategy, Limit restartLimit) {
		this.restartStrategy = restartStrategy;
		monitorLimit(restartLimit, true);
		this.restartLimit = restartLimit;
	}

	public final void registerLimit(AbstractGlobalSearchLimit limit) {
		limits.add(limit);
		final int lmask = limit.getLimitMask();
		if( (lmask & NEW_NODE) == NEW_NODE) {
			newNodeLimits.add(limit);
		}
		if( (lmask & END_NODE) == END_NODE) {
			endNodeLimits.add(limit);
		}
	}

	public abstract AbstractGlobalSearchLimit makeLimit(AbstractGlobalSearchStrategy strategy, Limit type, int limit);

	protected void addRestartLimit() {
		if(restartStrategy != null) {
			if(restartLimit == null) {
				LOGGER.log(Level.WARNING, "no limit associated with the restart strategy {0}", restartStrategy);
			} else {
				final AbstractGlobalSearchLimit lim = getLimit(restartLimit);
				if( lim == null) {
					LOGGER.log(Level.WARNING, "cant find the limit {0} associated with the restart strategy {1}", new Object[]{restartLimit, restartStrategy});}
				else {
					registerLimit(new RestartLimit(restartStrategy, lim));
				}
			}
		}
	}

	public void generateLimits() {
		TObjectIntProcedure<Limit> proc = new TObjectIntProcedure<Limit>() {
			@Override
			public boolean execute(Limit type, int value) {
				AbstractGlobalSearchLimit lim = makeLimit(searchStrategy, type, value);
				if(lim == null) {
					LOGGER.log(Level.WARNING, "cant create limit: {0}", type);
				}else {
					registerLimit(lim);
				}
				return true;
			}
		};
		solverSettings.forEachEntry(proc);
		addRestartLimit();
	}




	public final void setSearchStrategy(AbstractGlobalSearchStrategy searchStrategy) {
		this.searchStrategy = searchStrategy;
	}

	@Override
	public final AbstractGlobalSearchStrategy getSearchStrategy() {
		return searchStrategy;
	}


	@Override
	public final void initialize() {
		for (AbstractGlobalSearchLimit limit : limits) {
			limit.initialize();
		}
	}

	@Override
	public final void reset() {
		for (AbstractGlobalSearchLimit limit : limits) {
			limit.reset();
		}
	}

	@Override
	public final void newNode() throws ContradictionException {
		for (AbstractGlobalSearchLimit limit : newNodeLimits) {
			limit.newNode();
		}
	}

	@Override
	public final void endNode() throws ContradictionException {
		for (AbstractGlobalSearchLimit limit : endNodeLimits) {
			limit.endNode();
		}
	}

	private ListIterator<AbstractGlobalSearchLimit> iter;

	public final void writeLimits(Solution sol) {
		iter = limits.listIterator();
		while(iter.hasNext()) {
			sol.recordLimit(iter.nextIndex(), iter.next().getUpdatedNbAll());
		}
	}

	@Override
	public String pretty() {
		final StringBuilder stb = new StringBuilder();
		for (AbstractGlobalSearchLimit l : limits) {
			stb.append(l.pretty()).append(" ; ");
		}
		return stb.toString();
	}





}
